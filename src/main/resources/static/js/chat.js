let stompClient = null;
// Get current user from Thymeleaf - this is the key fix
let currentUser = /*[[${currentUser}]]*/ null;
let selectedUser = null;

console.log('Current user initialized as:', currentUser);

function connect() {
    console.log('Attempting to connect with user:', currentUser);

    if (!currentUser) {
        console.error('No current user found');
        updateConnectionStatus(false);
        return;
    }

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        console.log('Subscribing for user:', currentUser);
        updateConnectionStatus(true);

        // Subscribe to personal message queue
        stompClient.subscribe('/user/queue/messages', function(message) {
            console.log('Received message:', message.body);
            try {
                const chatNotification = JSON.parse(message.body);
                console.log('Parsed notification:', chatNotification);

                // Display message if it's from the currently selected user
                if (selectedUser === chatNotification.sender) {
                    displayMessage(chatNotification.sender, chatNotification.content, false);
                } else {
                    // Show notification for other users
                    showNewMessageNotification(chatNotification.sender);
                }
            } catch (e) {
                console.error('Error parsing message:', e);
            }
        });

        // Subscribe to public messages (if needed)
        stompClient.subscribe('/topic/public', function(message) {
            console.log('Received public message:', message.body);
        });

        loadUsers();
    }, function(error) {
        console.log('Connection error: ' + error);
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
    // You can implement visual notifications here
    console.log('New message from:', sender);
    // For example, you could update the user list to show unread messages
}

async function loadUsers() {
    try {
        const response = await fetch('/users');
        if (!response.ok) {
            throw new Error('Failed to load users');
        }

        const users = await response.json();
        console.log('Loaded users:', users);

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
    console.log('Selected user:', selectedUser);

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
        console.log('Loading chat history between', currentUser, 'and', selectedUser);
        const response = await fetch(`/messages/${currentUser}/${selectedUser}`);

        if (!response.ok) {
            throw new Error('Failed to load chat history');
        }

        const messages = await response.json();
        console.log('Loaded messages:', messages);

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

    console.log('Sending message:', chatMessage);

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

// Event listeners
document.getElementById('userSelect').addEventListener('change', onUserSelect);

document.getElementById('messageInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});

document.addEventListener('DOMContentLoaded', function() {
    console.log('Page loaded, current user:', currentUser);
    if (currentUser) {
        connect();
    } else {
        console.error('No current user available');
        updateConnectionStatus(false);
    }
});

// Handle page unload
window.addEventListener('beforeunload', function() {
    if (stompClient) {
        stompClient.disconnect();
    }
});