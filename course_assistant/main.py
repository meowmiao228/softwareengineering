import os
import dashscope
from dashscope import Generation
from dotenv import load_dotenv
import streamlit as st

# 加载环境变量
load_dotenv()

# 设置API密钥
dashscope.api_key = os.getenv('DASHSCOPE_API_KEY')

def get_ai_response(question: str, course_context: str = "通用") -> str:
    """
    调用大语言模型获取问答回答
    :param question: 用户提出的问题
    :param course_context: 课程上下文（数学/计算机等）
    :return: AI给出的解答内容
    """
    
    # 构建系统提示词，约束问答模式[reference:0]
    system_prompt = f"""
    你是一位{course_context}课程的专业助教。
    你的任务是根据学生提出的学科问题进行解答。
    - 要求回答详细、准确，提供解题步骤。
    - 如果问题与课程无关，请友好地引导学生回到课程话题。
    - 回答风格应温暖且富有鼓励性。
    """
    
    # 构建用户消息
    user_message = f"学生问道：{question}"
    
    # 调用通义千问模型
    response = Generation.call(
        model='qwen-max',           # 可选qwen-turbo, qwen-plus等
        messages=[
            {'role': 'system', 'content': system_prompt},
            {'role': 'user', 'content': user_message}
        ],
        temperature=0.6,            # 控制回答的创造性【0.0-1.0】[reference:1]
        result_format='message'     # 返回为消息格式
    )
    
    # 处理返回结果
    if response.status_code == 200:
        return response.output.choices[0].message.content
    else:
        return f"AI服务调用失败：{response.message}"

# Streamlit界面设计
def main():
    st.set_page_config(page_title="课程智能助教", page_icon="📚", layout="centered")
    
    # 侧边栏课程选择
    st.sidebar.title("⚙️ 课程设置")
    course_name = st.sidebar.selectbox(
        "选择课程",
        ["高等数学", "计算机科学导论", "大学物理", "英语写作", "其他"]
    )
    
    # 主界面
    st.title("📖 课程智能助教")
    st.markdown("我是你的专属课程助教，可以随时解答学习中的问题~")
    
    # 初始化对话历史
    if "messages" not in st.session_state:
        st.session_state.messages = []
    
    # 展示历史对话
    for msg in st.session_state.messages:
        with st.chat_message(msg["role"]):
            st.markdown(msg["content"])
    
    # 用户输入框
    user_input = st.chat_input("提出你的问题...")
    if user_input:
        # 显示用户输入
        with st.chat_message("user"):
            st.markdown(user_input)
        st.session_state.messages.append({"role": "user", "content": user_input})
        
        # 调用AI获取回复
        with st.chat_message("assistant"):
            with st.spinner("思考中..."):
                ai_response = get_ai_response(user_input, course_name)
                st.markdown(ai_response)
        st.session_state.messages.append({"role": "assistant", "content": ai_response})

if __name__ == "__main__":
    main()