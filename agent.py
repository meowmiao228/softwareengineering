import json
import random
import requests
from datetime import datetime
from flask import Flask, request, jsonify, render_template_string

# ==============================
# 基于 MCP 协议的能说会做智能体
# 团队仓库：https://github.com/meowmiao228/SoftwareEngineering.git
# ==============================
class MCPServer:
    def __init__(self):
        # 多轮对话记忆（加分项）
        self.memory = []
        
    # 工具1：查询天气（外部API，加分项）
    def get_weather(self, city: str) -> str:
        try:
            url = f"https://api.vvhan.com/api/weather?city={city}"
            res = requests.get(url, timeout=10)
            data = res.json()
            if data.get("success"):
                info = data["data"]
                return f"{city}当前天气：{info['type']}，气温{info['low']}~{info['high']}"
            return f"无法查询 {city} 的天气"
        except:
            return "天气服务暂时不可用"

    # 工具2：创建待办清单（文件操作）
    def create_todo(self, content: str) -> str:
        try:
            time_now = datetime.now().strftime("%Y-%m-%d %H:%M")
            todo = f"【待办事项】{time_now}\n{content}\n\n"
            with open("todo_list.txt", "a", encoding="utf-8") as f:
                f.write(todo)
            return f"✅ 待办已保存：{content}\n文件路径：todo_list.txt"
        except:
            return "❌ 保存失败"

    # 工具3：数学计算
    def calculate(self, expression: str) -> str:
        try:
            result = eval(expression)
            return f"计算结果：{result}"
        except:
            return "计算错误，请输入正确表达式"

    # MCP 核心：意图识别 + 自动执行
    def run_agent(self, user_input: str):
        self.memory.append({"用户": user_input})
        response = ""
        
        # 意图判断
        if "天气" in user_input:
            city = user_input.replace("天气", "").replace("查询", "").strip()
            city = city if city else "北京"
            response = self.get_weather(city)
            
        elif "待办" in user_input or "备忘录" in user_input:
            content = user_input.replace("待办", "").replace("备忘录", "").strip()
            response = self.create_todo(content)
            
        elif "计算" in user_input:
            exp = user_input.replace("计算", "").strip()
            response = self.calculate(exp)
            
        elif "你好" in user_input or "哈喽" in user_input:
            response = "你好！我是能说会做的AI智能体，我可以查天气、记待办、做计算~"
            
        else:
            response = f"我收到啦：{user_input}！你可以让我查天气、记待办、做计算哦~"

        self.memory.append({"智能体": response})
        return response

# ==============================
# 网页前端界面（加分项）
# ==============================
app = Flask(__name__)
mcp_agent = MCPServer()

# 简易聊天界面
WEB_UI = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>能说会做的AI智能体</title>
</head>
<body>
    <h2>🤖 能说会做的AI智能体</h2>
    <div id="chat-box" style="height:350px;overflow-y:auto;border:1px solid #ccc;padding:12px;"></div>
    <input type="text" id="user-input" placeholder="输入指令..." style="width:80%;margin-top:10px;">
    <button onclick="sendMessage()">发送</button>

    <script>
        function sendMessage() {
            let msg = document.getElementById("user-input").value;
            if(!msg) return;
            let box = document.getElementById("chat-box");
            box.innerHTML += "<p><b>你：</b>"+msg+"</p>";
            
            fetch("/chat", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ message: msg })
            }).then(res => res.json()).then(data => {
                box.innerHTML += "<p><b>🤖 助手：</b>"+data.reply+"</p>";
                document.getElementById("user-input").value = "";
            });
        }
    </script>
</body>
</html>
"""

@app.route('/')
def index():
    return render_template_string(WEB_UI)

@app.route('/chat', methods=['POST'])
def chat():
    data = request.get_json()
    reply = mcp_agent.run_agent(data['message'])
    return jsonify({"reply": reply})

if __name__ == "__main__":
    print("✅ 智能体启动成功！访问：http://127.0.0.1:5000")
    print("✅ 团队仓库：https://github.com/meowmiao228/SoftwareEngineering.git")
    app.run(host="0.0.0.0", port=5000, debug=True)