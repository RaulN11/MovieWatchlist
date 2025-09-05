'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let nickname = null;
let fullname = null;
let selectedUserId = null;

function connect(event) {
    nickname = document.querySelector('#nickname').value.trim();
    fullname = document.querySelector('#fullname').value.trim();

    if (nickname && fullname) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);

    // Register the connected user
    stompClient.send("/app/user.addUser", {}, JSON.stringify({nickName: nickname, fullName: fullname, status: 'ONLINE'}));

    document.querySelector('#connected-user-fullname').textContent = fullname;
    findAndDisplayConnectedUsers();
}

async function findAndDisplayConnectedUsers() {
    const response = await fetch('/users');
    let users = await response.json();
    users = users.filter(u => u.nickName !== nickname);

    const list = document.getElementById('connectedUsers');
    list.innerHTML = '';

    users.forEach(user => {
        const li = document.createElement('li');
        li.classList.add('user-item');
        li.id = user.nickName;

        const img = document.createElement('img');
        img.src = '../img/user_icon.png';
        img.alt = user.fullName;

        const span = document.createElement('span');
        span.textContent = user.fullName;

        const badge = document.createElement('span');
        badge.classList.add('nbr-msg', 'hidden');
        badge.textContent = '0';

        li.appendChild(img);
        li.appendChild(span);
        li.appendChild(badge);

        li.addEventListener('click', userItemClick);

        list.appendChild(li);
        list.appendChild(document.createElement('hr'));
    });
}

function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => item.classList.remove('active'));
    messageForm.classList.remove('hidden');

    const clicked = event.currentTarget;
    clicked.classList.add('active');
    selectedUserId = clicked.getAttribute('id');

    fetchAndDisplayUserChat();

    const badge = clicked.querySelector('.nbr-msg');
    badge.classList.add('hidden');
    badge.textContent = '0';
}

function displayMessage(sender, content) {
    const div = document.createElement('div');
    div.classList.add('message');
    div.classList.add(sender === nickname ? 'sender' : 'receiver');

    const p = document.createElement('p');
    p.textContent = content;
    div.appendChild(p);

    chatArea.appendChild(div);
    chatArea.scrollTop = chatArea.scrollHeight;
}

async function fetchAndDisplayUserChat() {
    const response = await fetch(`/messages/${nickname}/${selectedUserId}`);
    const chats = await response.json();
    chatArea.innerHTML = '';
    chats.forEach(chat => displayMessage(chat.sender, chat.content));
}

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    findAndDisplayConnectedUsers();

    if (selectedUserId && selectedUserId === message.sender) {
        displayMessage(message.sender, message.content);
    }

    const notified = document.querySelector(`#${message.sender}`);
    if (notified && !notified.classList.contains('active')) {
        const badge = notified.querySelector('.nbr-msg');
        badge.classList.remove('hidden');
        badge.textContent = '';
    }
}

function sendMessage(event) {
    const content = messageInput.value.trim();
    if (content && stompClient) {
        const chatMessage = {
            sender: nickname,
            recipient: selectedUserId,
            content: content,
            timestamp: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(nickname, content);
        messageInput.value = '';
    }
    event.preventDefault();
}

function onLogout() {
    stompClient.send("/app/user.disconnectUser", {}, JSON.stringify({nickName: nickname, fullName: fullname, status: 'OFFLINE'}));
    window.location.reload();
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.onbeforeunload = () => onLogout();
