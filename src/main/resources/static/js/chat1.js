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
        placeholder.style.display="none";
        chatContent.style.display="flex";
        chatContent.style.flexDirection="column";
        users.forEach(u=>u.classList.remove("active"));
        user.classList.add("active");
        const name=user.querySelector(".small-name").textContent;
        document.querySelector(".chatting-user-name").textContent=name;
        const profilePic=user.querySelector(".small-profile").src;
        document.querySelector(".chatting-user-pic").src=profilePic;
    })
})
