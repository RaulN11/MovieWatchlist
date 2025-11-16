const users=document.querySelectorAll(".user");
const placeholder=document.getElementById("placeholder")
const chatContent=document.getElementById("chat-content")
let stompClient=null;
let currentReceiver=null;
let currentSender=window.currentSender;

function connect(){
    const socket=new SockJS('/ws');
    stompClient=Stomp.over(socket);
    stompClient.connect({}, () => {
        console.log("WebSocket connected");
    });
}
connect();

users.forEach(user=>{
    user.addEventListener("click",()=>{
        loadChatHistory(currentSender, currentReceiver);
        placeholder.style.display="none";
        chatContent.style.display="flex";
        chatContent.style.flexDirection="column";
        users.forEach(u=>u.classList.remove("active"));
        user.classList.add("active");
        const name=user.querySelector(".small-name").textContent;
        currentReceiver=name
        document.querySelector(".chatting-user-name").textContent=name;
        const profilePic=user.querySelector(".small-profile").src;
        document.querySelector(".chatting-user-pic").src=profilePic;
    })
})
function setupSendMessage(){
    const sendButton=document.getElementById("send-button");
    const messageField=document.querySelector(".message-field")
    sendButton.addEventListener("click", ()=>{
        const content=messageField.value.trim();
        if(content === "" || !currentReceiver) return;
        const chatMessage={
            senderName:currentSender,
            receiverName:currentReceiver,
            content:content
        };
        displayIncomingMessage(chatMessage);
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        messageField.value="";
    });
}
setupSendMessage();

function displayIncomingMessage(message){
    if(message.senderName !== currentReceiver) return;
    const chatTexts=document.querySelector(".chat-texts");
    const msgDiv= document.createElement("div");
    msgDiv.classList.add("message", "incoming-message");
    msgDiv.textContent=message.content;
    chatTexts.appendChild(msgDiv);
    chatTexts.scrollTop=chatTexts.scrollHeight;
}
function displayOutgoingMessage(message){
    const chatTexts=document.querySelector(".chat-texts");
    const msgDiv=document.createElement("div");
    msgDiv.classList.add("message", "outgoing-message");
    msgDiv.textContent=message.content;
    chatTexts.appendChild(msgDiv);
    chatTexts.scrollTop=chatTexts.scrollHeight;
}
function loadChatHistory(sender, receiver){
    fetch('/messages/${sender}/${receiver}')
        .then(res=>res.json())
        .then(messages=>{
            const chatTexts=document.querySelector(".chat-texts")
            chatTexts.innerHTML="";
            messages.forEach(msg=>{
                if(msg.senderName===sender){
                    displayOutgoingMessage(msg);
                }else {
                    displayIncomingMessage(msg);
                }
            });
        });
}

