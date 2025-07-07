from flask import Flask, jsonify, request, Response
from datetime import datetime
import json
import time
import random
import logging

app = Flask(__name__)
logging.basicConfig(level=logging.INFO) # 로깅 레벨 설정

# --- MCP 도구 정의 (기존과 동일하게 HTTP 요청-응답 방식) ---
TOOLS_DEFINITION = {
    "tools": [
        {
            "type": "function",
            "function": {
                "name": "get_current_time",
                "description": "현재 서버의 로컬 시간을 'YYYY-MM-DD HH:MM:SS' 형식으로 반환합니다.",
                "parameters": {
                    "type": "object",
                    "properties": {},
                    "required": []
                }
            }
        }
    ]
}

# 1. /.well-known/mcp 엔드포인트: 도구 정의를 제공합니다.
@app.route("/.well-known/mcp", methods=["GET"])
def get_mcp_tools():
    app.logger.info("/.well-known/mcp 엔드포인트 요청 수신. 도구 정의 반환.")
    return jsonify(TOOLS_DEFINITION)

# 2. 도구 호출 엔드포인트: 도구 이름으로 호출을 라우팅합니다.
@app.route("/tools/<tool_name>", methods=["POST"])
def call_tool(tool_name):
    app.logger.info(f"/tools/{tool_name} 엔드포인트 요청 수신.")
    if request.method == "POST":
        try:
            args = request.get_json(silent=True)
            if args is None:
                args = {}

            app.logger.info(f"도구 '{tool_name}' 호출. 인수: {args}")

            if tool_name == "get_current_time":
                current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                result = {"current_time": current_time}
                app.logger.info(f"'{tool_name}' 결과: {result}")
                return jsonify(result)
            else:
                app.logger.warning(f"알 수 없는 도구 요청: {tool_name}")
                return jsonify({"error": f"Unknown tool: {tool_name}"}), 404
        except Exception as e:
            app.logger.error(f"도구 호출 중 오류 발생: {e}", exc_info=True)
            return jsonify({"error": str(e)}), 500
    else:
        app.logger.warning(f"지원되지 않는 메소드: {request.method} for /tools/{tool_name}")
        return jsonify({"error": "Method Not Allowed"}), 405

# --- SSE 스트리밍 엔드포인트 추가 ---
@app.route("/sse", methods=["GET"])
def sse_stream():
    app.logger.info("/sse 엔드포인트 요청 수신. SSE 스트림 시작.")

    def generate_events():
        while True:
            # 현재 시간과 무작위 숫자를 포함하는 이벤트 데이터 생성
            current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            random_number = random.randint(1, 100)
            event_data = {
                "timestamp": current_time,
                "message": "Current server status update",
                "randomNumber": random_number
            }
            # SSE 형식: data: <JSON 문자열>\n\n
            yield f"data: {json.dumps(event_data)}\n\n"
            time.sleep(1) # 1초마다 데이터 전송

    # MIME 타입 'text/event-stream'으로 Response 객체 생성
    return Response(generate_events(), mimetype="text/event-stream")

if __name__ == "__main__":
    # Flask 앱을 5001번 포트에서 실행합니다. (이전 5000에서 변경, 충돌 방지 및 테스트 편의)
    app.run(host='0.0.0.0', port=5001, debug=True)