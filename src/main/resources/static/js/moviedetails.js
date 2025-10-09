document.addEventListener('DOMContentLoaded', () => {

    const buttons = [
        { id: 'like-button', addUrl: '/client/addtoliked/', removeUrl: '/client/removefromliked/' },
        { id: 'watched-button', addUrl: '/client/addtowatched?title=', removeUrl: '/client/removefromwatched/' },
        { id: 'watchlist-button', addUrl: '/client/addtowatchlist/', removeUrl: '/client/removefromwatchlist/' }
    ];
    const modal = document.getElementById('review-modal');
    const closeBtn = document.querySelector('.close-btn');
    const cancelBtn = document.getElementById('cancel');
    const saveBtn = document.getElementById('save');
    const textarea = document.querySelector('.review-text');

    buttons.forEach(btnConfig => {
        const btn = document.getElementById(btnConfig.id);
        if (!btn) return;

        const icon = btn.querySelector('i');
        icon.style.color = btn.dataset.state === 'active' ? '#710a42' : 'white';

        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            const title = btn.dataset.title;
            const state = btn.dataset.state;

            // Handle fetch toggle
            const url = state === 'inactive'
                ? btnConfig.addUrl + encodeURIComponent(title)
                : btnConfig.removeUrl + encodeURIComponent(title);
            const method = state === 'inactive' ? 'POST' : 'DELETE';

            try {
                const response = await fetch(url, {
                    method,
                    headers: { 'Content-Type': 'application/json' }
                });

                if (!response.ok) throw new Error('Network error');

                if (state === 'inactive') {
                    btn.dataset.state = 'active';
                    icon.style.color = '#710a42';
                    if (btnConfig.id === 'watched-button') {
                        modal.style.display = 'flex';
                        document.body.style.overflow = 'hidden'; 
                    }

                } else {
                    btn.dataset.state = 'inactive';
                    icon.style.color = 'white';
                }

            } catch (err) {
                console.error(err);
            }
        });
    });

    // Close modal when clicking × or Cancel
    const closeModal = () => {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
        textarea.value = ''; // reset text
    };

    closeBtn?.addEventListener('click', closeModal);
    cancelBtn?.addEventListener('click', closeModal);

    // Optional: clicking outside modal closes it
    window.addEventListener('click', (e) => {
        if (e.target === modal) closeModal();
    });

    // Save button handler (for now, just close modal)
    saveBtn?.addEventListener('click', () => {
        const reviewText = textarea.value.trim();
        console.log('Review saved:', reviewText);
        // TODO: send reviewText to backend via fetch()
        closeModal();
    });
});
