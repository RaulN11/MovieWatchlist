let stompClient = null;
let currentUser = window.currentUser;
let selectedUser = null;


function connect() {
    if (!currentUser) {
        console.error('No current user found');
        updateConnectionStatus(false);
        return;
    }

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        updateConnectionStatus(true);
        stompClient.subscribe('/user/queue/messages', function(message) {
            try {
                const chatNotification = JSON.parse(message.body);
                if (chatNotification.sender !== currentUser) {
                    if (selectedUser === chatNotification.sender) {
                        displayMessage(chatNotification.sender, chatNotification.content, false);
                    } else {
                        showNewMessageNotification(chatNotification.sender);
                    }
                }
            } catch (e) {
                console.error('Error parsing message:', e);
            }
        });


        stompClient.subscribe('/topic/public', function(message) {
        });

        loadUsers();
    }, function(error) {
        updateConnectionStatus(false);
        setTimeout(connect, 5000);
    });
}

function updateConnectionStatus(connected) {
    const statusElement = document.getElementById('connectionStatus');
    statusElement.innerHTML = connected
        ? '<span class="connected">✓ Connected</span>'
        : '<span class="disconnected">✗ Disconnected - Retrying...</span>';
}

function showNewMessageNotification(sender) {
}

async function loadUsers() {
    try {
        const response = await fetch('/users');
        if (!response.ok) {
            throw new Error('Failed to load users');
        }

        const users = await response.json();

        const userSelect = document.getElementById('userSelect');
        userSelect.innerHTML = '<option value="">Choose a user...</option>';

        users.forEach(username => {
            if (username !== currentUser) {
                const option = document.createElement('option');
                option.value = username;
                option.textContent = username;
                userSelect.appendChild(option);
            }
        });
    } catch (error) {
        console.error('Error loading users:', error);
    }
}

function onUserSelect() {
    const userSelect = document.getElementById('userSelect');
    selectedUser = userSelect.value;
    const messageInput = document.getElementById('messageInput');
    const sendButton = document.getElementById('sendButton');
    if (selectedUser) {
        messageInput.disabled = false;
        sendButton.disabled = false;
        messageInput.focus();
        loadChatHistory();
    } else {
        messageInput.disabled = true;
        sendButton.disabled = true;
        document.getElementById('messagesContainer').innerHTML =
            '<div style="text-align: center; color: #666; margin-top: 50px;">Select a user to start chatting</div>';
    }
}

async function loadChatHistory() {
    if (!selectedUser) return;

    try {
        const response = await fetch(`/messages/${currentUser}/${selectedUser}`);

        if (!response.ok) {
            throw new Error('Failed to load chat history');
        }

        const messages = await response.json();

        const messagesContainer = document.getElementById('messagesContainer');
        messagesContainer.innerHTML = '';

        if (messages.length === 0) {
            messagesContainer.innerHTML = '<div style="text-align: center; color: #666;">No messages yet. Start the conversation!</div>';
        } else {
            messages.forEach(message => {
                displayMessage(message.sender, message.content, message.sender === currentUser);
            });
        }

        scrollToBottom();
    } catch (error) {
        console.error('Error loading chat history:', error);
    }
}

function displayMessage(sender, content, isSent) {
    const messagesContainer = document.getElementById('messagesContainer');

    if (messagesContainer.innerHTML.includes('No messages yet') || messagesContainer.innerHTML.includes('Select a user')) {
        messagesContainer.innerHTML = '';
    }

    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${isSent ? 'sent' : 'received'}`;

    const senderDiv = document.createElement('div');
    senderDiv.className = 'message-sender';
    senderDiv.textContent = isSent ? 'You' : sender;

    const contentDiv = document.createElement('div');
    contentDiv.textContent = content;

    messageDiv.appendChild(senderDiv);
    messageDiv.appendChild(contentDiv);
    messagesContainer.appendChild(messageDiv);

    scrollToBottom();
}

function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const content = messageInput.value.trim();

    if (!content || !selectedUser || !stompClient || !currentUser) {
        console.error('Cannot send message:', {
            content: !!content,
            selectedUser: !!selectedUser,
            stompClient: !!stompClient,
            currentUser: !!currentUser
        });
        return;
    }

    const chatMessage = {
        sender: currentUser,
        receiver: selectedUser,
        content: content,
        timestamp: new Date().toISOString()
    };

    try {
        stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
        displayMessage(currentUser, content, true);
        messageInput.value = '';
    } catch (error) {
        console.error('Error sending message:', error);
    }
}

function scrollToBottom() {
    const messagesContainer = document.getElementById('messagesContainer');
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

document.getElementById('userSelect').addEventListener('change', onUserSelect);

document.getElementById('messageInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});

document.addEventListener('DOMContentLoaded', function() {
    if (currentUser) {
        connect();
    } else {
        console.error('No current user available');
        updateConnectionStatus(false);
    }
});
window.addEventListener('beforeunload', function() {
    if (stompClient) {
        stompClient.disconnect();
    }
});