"""PostToolUse hook: formats an edited Kotlin file with the project's Spotless (ktlint) setup."""

import json
import os
import subprocess
import sys

KOTLIN_EXTENSIONS = (".kt", ".kts")

path = json.load(sys.stdin).get("tool_input", {}).get("file_path", "")
if not path.endswith(KOTLIN_EXTENSIONS):
    sys.exit(0)

project_dir = os.environ.get("CLAUDE_PROJECT_DIR", os.getcwd())
gradlew = os.path.join(project_dir, "gradlew.bat" if os.name == "nt" else "gradlew")
result = subprocess.run(
    [gradlew, "spotlessApply", f"-PspotlessIdeHook={path}", "--quiet"],
    cwd=project_dir,
    capture_output=True,
    text=True,
)
if result.returncode != 0:
    # Exit code 2 shows the error to Claude, so it can fix what ktlint can't auto-format.
    print(result.stderr[-2000:], file=sys.stderr)
    sys.exit(2)
