import os
import time
import random
import tempfile
import shutil
from pathlib import Path
from urllib.parse import urlparse

from fastapi import FastAPI, UploadFile, File, HTTPException
from pydantic import BaseModel
from google import genai
from dotenv import load_dotenv


# ============================================================
# LOAD ENVIRONMENT VARIABLES
# ============================================================

load_dotenv()


# ============================================================
# FASTAPI APP
# ============================================================

app = FastAPI(
    title="Signal AI Backend",
    version="2.2.0"
)


# ============================================================
# REQUEST / RESPONSE MODELS
# ============================================================

class AnalyzeRequest(BaseModel):
    content: str


class SocialAnalyzeRequest(BaseModel):
    url: str


class AnalyzeResponse(BaseModel):
    title: str
    summary: str
    keywords: str
    category: str
    target_location: str


# ============================================================
# GEMINI CLIENT
# ============================================================

def get_gemini_client():

    api_key = os.getenv("GEMINI_API_KEY")

    if not api_key:
        raise Exception(
            "GEMINI_API_KEY is missing from .env"
        )

    return genai.Client(
        api_key=api_key
    )


# ============================================================
# ROOT
# ============================================================

@app.get("/")
def root():

    return {
        "status": "Signal backend is running",
        "version": "2.2.0"
    }


# ============================================================
# PARSE AI RESPONSE
# ============================================================

def parse_response(text: str) -> AnalyzeResponse:

    def extract(label: str) -> str:

        marker = f"{label}:"

        start = text.find(marker)

        if start == -1:
            return ""

        value_start = start + len(marker)

        next_line = text.find(
            "\n",
            value_start
        )

        if next_line == -1:

            return (
                text[value_start:]
                .strip()
            )

        return (
            text[
                value_start:next_line
            ]
            .strip()
        )


    return AnalyzeResponse(

        title=(
                extract("TITLE")
                or "Saved memory"
        ),

        summary=(
                extract("SUMMARY")
                or "Content saved in Signal."
        ),

        keywords=(
                extract("KEYWORDS")
                or "saved content"
        ),

        category=(
                extract("CATEGORY")
                or "General"
        ),

        target_location=(
                extract("TARGET_LOCATION")
                or "NONE"
        )
    )


# ============================================================
# CHECK TEMPORARY GEMINI ERROR
# ============================================================

def is_retryable_error(error: Exception) -> bool:

    error_text = str(error).upper()

    retryable_codes = [

        "503",
        "UNAVAILABLE",

        "429",
        "RESOURCE_EXHAUSTED",

        "500",
        "INTERNAL",

        "502",
        "BAD GATEWAY",

        "504",
        "DEADLINE_EXCEEDED",

        "TIMEOUT"
    ]

    return any(
        code in error_text
        for code in retryable_codes
    )


# ============================================================
# GEMINI GENERATION WITH RETRIES + MODEL FALLBACK
# ============================================================

def generate_with_retry(
        client,
        contents,
        purpose: str = "content"
):

    models = [

        "gemini-3.7-flash",

        "gemini-3.6-flash",

        "gemini-2.5-flash"
    ]


    last_error = None


    for model in models:

        print()
        print("=" * 60)
        print(
            f"TRYING MODEL: {model}"
        )
        print(
            f"PURPOSE: {purpose}"
        )
        print("=" * 60)


        for attempt in range(4):

            try:

                print()

                print(
                    f"Attempt "
                    f"{attempt + 1}/4 "
                    f"using {model}"
                )


                response = (

                    client.models.generate_content(

                        model=model,

                        contents=contents
                    )
                )


                print()
                print("=" * 60)
                print(
                    f"SUCCESS WITH MODEL: {model}"
                )
                print("=" * 60)


                return response


            except Exception as e:

                last_error = e


                print()

                print(
                    "GEMINI ERROR:"
                )

                print(
                    str(e)
                )


                if not is_retryable_error(e):

                    print()

                    print(
                        "This does not appear to be "
                        "a temporary Gemini error."
                    )

                    print(
                        "Moving to next model..."
                    )

                    break


                if attempt < 3:

                    delay = (

                            (2 ** (attempt + 1))

                            +

                            random.uniform(
                                0,
                                1.5
                            )
                    )


                    print()

                    print(
                        "Temporary Gemini error detected."
                    )

                    print(
                        f"Waiting {delay:.1f} seconds "
                        "before retry..."
                    )


                    time.sleep(
                        delay
                    )


        print()

        print(
            f"MODEL FAILED: {model}"
        )

        print(
            "TRYING NEXT MODEL..."
        )


    raise Exception(

        "All Gemini models failed. "

        f"Last error: {last_error}"
    )


# ============================================================
# NORMAL TEXT / URL ANALYSIS
# ============================================================

@app.post(
    "/analyze",
    response_model=AnalyzeResponse
)
def analyze_content(
        request: AnalyzeRequest
):

    try:

        client = get_gemini_client()


        prompt = f"""
You are Signal, an AI personal memory assistant.

Analyze the following saved content.

Return exactly in this format:

TITLE:
SUMMARY:
KEYWORDS:
CATEGORY:
TARGET_LOCATION:

Rules:

- TITLE: short and useful
- SUMMARY: maximum 2 sentences
- KEYWORDS: 5 to 10 useful keywords separated by commas
- CATEGORY: choose one useful category
- TARGET_LOCATION: identify a real-world place only if clearly mentioned
- If there is no location, write NONE

Saved content:

{request.content}
"""


        response = generate_with_retry(

            client=client,

            contents=prompt,

            purpose="normal content analysis"
        )


        text = (

                response.text

                or ""
        )


        print()

        print(
            "NORMAL ANALYSIS RESULT:"
        )

        print(
            text
        )


        return parse_response(
            text
        )


    except Exception as e:

        print()

        print(
            "NORMAL ANALYSIS ERROR:"
        )

        print(
            str(e)
        )


        raise HTTPException(

            status_code=500,

            detail=str(e)
        )


# ============================================================
# ANALYZE UPLOADED VIDEO
# ============================================================

@app.post(
    "/analyze-video",
    response_model=AnalyzeResponse
)
async def analyze_video(
        file: UploadFile = File(...)
):

    temp_dir = None

    uploaded_file = None


    try:

        print()
        print("=" * 60)
        print(
            "VIDEO ANALYSIS STARTED"
        )
        print("=" * 60)


        client = get_gemini_client()


        temp_dir = tempfile.mkdtemp(
            prefix="signal_video_"
        )


        file_path = os.path.join(

            temp_dir,

            file.filename
            or "video.mp4"
        )


        with open(
                file_path,
                "wb"
        ) as output:

            while True:

                chunk = await file.read(
                    1024 * 1024
                )

                if not chunk:

                    break

                output.write(
                    chunk
                )


        print(
            f"Video saved: {file_path}"
        )


        print(
            "Uploading video to Gemini..."
        )


        uploaded_file = (

            client.files.upload(

                file=file_path
            )
        )


        max_processing_seconds = 600

        processing_start = time.time()


        while True:

            file_info = client.files.get(

                name=uploaded_file.name
            )


            state = (

                getattr(
                    file_info.state,
                    "name",
                    str(file_info.state)
                )
            )


            print(
                f"Gemini file state: {state}"
            )


            if state == "ACTIVE":

                uploaded_file = (
                    file_info
                )

                break


            if state == "FAILED":

                raise Exception(
                    "Gemini failed to process video."
                )


            elapsed = (

                    time.time()

                    -

                    processing_start
            )


            if elapsed > max_processing_seconds:

                raise Exception(
                    "Gemini video processing "
                    "timed out after 10 minutes."
                )


            print(
                "Gemini is processing video..."
            )


            time.sleep(
                5
            )


        prompt = """
You are Signal, an AI personal memory assistant.

Analyze this video carefully.

Use:

- visual content
- spoken audio
- on-screen text
- captions if visible
- signs
- dates
- locations
- names
- events

Return exactly in this format:

TITLE:
SUMMARY:
KEYWORDS:
CATEGORY:
TARGET_LOCATION:

Rules:

- TITLE: short and useful
- SUMMARY: maximum 2 sentences
- KEYWORDS: 5 to 10 useful keywords separated by commas
- CATEGORY: choose the most useful category
- TARGET_LOCATION: identify a real-world location only if clearly supported
- If no location is clearly present, write NONE
- Do not invent dates, locations, names, or events
"""


        print(
            "Analyzing video with Gemini..."
        )


        response = generate_with_retry(

            client=client,

            contents=[

                uploaded_file,

                prompt
            ],

            purpose="uploaded video analysis"
        )


        text = (

                response.text

                or ""
        )


        print()

        print(
            "VIDEO ANALYSIS RESULT:"
        )

        print()

        print(
            text
        )


        return parse_response(
            text
        )


    except Exception as e:

        print()

        print(
            "VIDEO ANALYSIS ERROR:"
        )

        print(
            str(e)
        )


        raise HTTPException(

            status_code=500,

            detail=str(e)
        )


    finally:

        try:

            if uploaded_file:

                client.files.delete(

                    name=uploaded_file.name
                )

        except Exception:

            pass


        if temp_dir:

            try:

                shutil.rmtree(

                    temp_dir,

                    ignore_errors=True
                )

            except Exception:

                pass


# ============================================================
# DETECT YOUTUBE URL
# ============================================================

def is_youtube_url(
        url: str
) -> bool:

    hostname = (

        urlparse(url)
        .netloc
        .lower()
    )


    return (

            "youtube.com" in hostname

            or

            "youtu.be" in hostname

    )


# ============================================================
# DOWNLOAD VIDEO
#
# INSTAGRAM:
#   Existing normal yt-dlp behaviour.
#
# YOUTUBE:
#   Firefox cookies
#   Deno JavaScript challenge solver
#   EJS remote components
# ============================================================

def download_social_video(
        url: str,
        output_dir: str
) -> str:

    import yt_dlp


    print()

    print(
        "Downloading social video..."
    )

    print(
        f"URL: {url}"
    )


    output_template = os.path.join(

        output_dir,

        "social_video.%(ext)s"
    )


    # ========================================================
    # COMMON OPTIONS
    # ========================================================

    ydl_opts = {

        "outtmpl": output_template,

        # Download best video + best audio when available.
        "format": "bv*+ba/b",

        # Merge streams using FFmpeg.
        "merge_output_format": "mp4",

        "noplaylist": True,

        "retries": 5,

        "fragment_retries": 5,

        "socket_timeout": 60,

        "abort_on_error": True
    }


    # ========================================================
    # YOUTUBE ONLY CONFIGURATION
    #
    # DO NOT APPLY THIS TO INSTAGRAM.
    # ========================================================

    if is_youtube_url(url):

        print()

        print("=" * 60)

        print(
            "YOUTUBE URL DETECTED"
        )

        print(
            "Using Firefox cookies + Deno/EJS solver"
        )

        print("=" * 60)


        ydl_opts.update({

            # Your successful terminal test used Firefox.
            "cookiesfrombrowser": (
                "firefox",
            ),

            # Enable EJS remote challenge solver.
            "remote_components": (
                "ejs:github"
            ),

            # Let yt-dlp use Deno automatically.
            "js_runtimes": {

                "deno": {}
            },

            # YouTube client configuration.
            "extractor_args": {

                "youtube": {

                    "player_client": [

                        "web_embedded",

                        "tv"
                    ]
                }
            }
        })


    # ========================================================
    # INSTAGRAM / OTHER SOCIAL SITES
    #
    # No Firefox cookies.
    # No Deno configuration.
    # Existing behaviour remains isolated.
    # ========================================================

    else:

        print()

        print(
            "NON-YOUTUBE SOCIAL URL DETECTED"
        )

        print(
            "Using existing social download configuration."
        )


    # ========================================================
    # DOWNLOAD
    # ========================================================

    with yt_dlp.YoutubeDL(
            ydl_opts
    ) as ydl:

        info = ydl.extract_info(

            url,

            download=True
        )


    # ========================================================
    # FIND FINAL VIDEO FILE
    # ========================================================

    video_extensions = [

        "*.mp4",

        "*.mkv",

        "*.webm",

        "*.mov"
    ]


    possible_files = []


    for extension in video_extensions:

        possible_files.extend(

            Path(
                output_dir
            ).glob(
                extension
            )
        )


    # Avoid partial download files.

    possible_files = [

        file

        for file in possible_files

        if not str(
            file
        ).endswith(
            ".part"
        )
    ]


    if not possible_files:

        raise Exception(
            "Video download completed but "
            "no final video file was found."
        )


    # Prefer the largest final media file.
    #
    # This avoids accidentally selecting
    # a tiny metadata or thumbnail-related file.

    video_file = max(

        possible_files,

        key=lambda file: (
            file.stat()
            .st_size
        )
    )


    video_path = str(
        video_file
    )


    print()

    print(
        f"Downloaded video: {video_path}"
    )


    return video_path


# ============================================================
# ANALYZE INSTAGRAM / YOUTUBE / SOCIAL URL
# ============================================================

@app.post(
    "/analyze-social",
    response_model=AnalyzeResponse
)
def analyze_social(
        request: SocialAnalyzeRequest
):

    temp_dir = None

    uploaded_file = None

    client = None


    try:

        print()

        print("=" * 60)

        print(
            "SOCIAL VIDEO ANALYSIS STARTED"
        )

        print("=" * 60)


        print(
            f"URL: {request.url}"
        )


        if is_youtube_url(
                request.url
        ):

            print(
                "SOURCE: YOUTUBE"
            )

        else:

            print(
                "SOURCE: INSTAGRAM / OTHER SOCIAL"
            )


        client = get_gemini_client()


        # ====================================================
        # CREATE TEMP DIRECTORY
        # ====================================================

        temp_dir = tempfile.mkdtemp(

            prefix="signal_social_"
        )


        # ====================================================
        # DOWNLOAD VIDEO
        # ====================================================

        video_path = download_social_video(

            url=request.url,

            output_dir=temp_dir
        )


        # ====================================================
        # UPLOAD VIDEO TO GEMINI
        # ====================================================

        print()

        print(
            "Uploading video to Gemini..."
        )


        uploaded_file = (

            client.files.upload(

                file=video_path
            )
        )


        # ====================================================
        # WAIT FOR GEMINI VIDEO PROCESSING
        # ====================================================

        max_processing_seconds = 600

        processing_start = time.time()


        while True:

            file_info = client.files.get(

                name=uploaded_file.name
            )


            state = (

                getattr(

                    file_info.state,

                    "name",

                    str(
                        file_info.state
                    )
                )
            )


            print(
                f"Gemini video state: {state}"
            )


            if state == "ACTIVE":

                print(
                    "Gemini video processing complete."
                )


                uploaded_file = (
                    file_info
                )

                break


            if state == "FAILED":

                raise Exception(

                    "Gemini failed to process "
                    "the downloaded social video."
                )


            elapsed = (

                    time.time()

                    -

                    processing_start
            )


            if elapsed > max_processing_seconds:

                raise Exception(

                    "Gemini video processing "
                    "timed out after 10 minutes."
                )


            print(
                "Gemini is processing video..."
            )


            time.sleep(
                5
            )


        # ====================================================
        # SOCIAL VIDEO PROMPT
        # ====================================================

        prompt = """
You are Signal, an AI personal memory assistant.

Analyze this social media video carefully.

Understand:

- spoken audio
- visuals
- on-screen text
- subtitles
- people
- places
- events
- dates
- deadlines
- announcements
- products
- restaurants
- travel locations
- actionable information

Return exactly in this format:

TITLE:
SUMMARY:
KEYWORDS:
CATEGORY:
TARGET_LOCATION:

Rules:

- TITLE: short, specific and useful
- SUMMARY: maximum 2 sentences
- KEYWORDS: 5 to 10 useful keywords separated by commas
- CATEGORY: choose one useful category
- TARGET_LOCATION: write a real-world location only when clearly supported
- If no clear location exists, write NONE
- Do not invent information
- Focus on what a user would want to remember from this video
"""


        # ====================================================
        # ANALYZE VIDEO WITH GEMINI
        # ====================================================

        print()

        print(
            "Analyzing social video with Gemini..."
        )


        response = generate_with_retry(

            client=client,

            contents=[

                uploaded_file,

                prompt
            ],

            purpose="social video analysis"
        )


        text = (

                response.text

                or ""
        )


        print()

        print("=" * 60)

        print(
            "SOCIAL VIDEO ANALYSIS RESULT:"
        )

        print("=" * 60)

        print()

        print(
            text
        )


        return parse_response(
            text
        )


    except Exception as e:

        print()

        print("=" * 60)

        print(
            "SOCIAL VIDEO ANALYSIS ERROR:"
        )

        print(
            str(e)
        )

        print("=" * 60)


        raise HTTPException(

            status_code=500,

            detail=str(e)
        )


    finally:

        # ====================================================
        # DELETE GEMINI FILE
        # ====================================================

        try:

            if (

                    client is not None

                    and

                    uploaded_file is not None
            ):

                client.files.delete(

                    name=uploaded_file.name
                )


                print(
                    "Gemini temporary file deleted."
                )


        except Exception as cleanup_error:

            print(
                "Gemini file cleanup skipped:"
            )

            print(
                cleanup_error
            )


        # ====================================================
        # DELETE LOCAL TEMP FILES
        # ====================================================

        if temp_dir:

            try:

                shutil.rmtree(

                    temp_dir,

                    ignore_errors=True
                )


                print(
                    "Temporary social files cleaned."
                )


            except Exception:

                pass