document.addEventListener('DOMContentLoaded', () => {
    const buttons = [
        { id: 'like-button', addUrl: '/client/addtoliked?tid=', removeUrl: '/client/removefromliked?tid=' },
        { id: 'watched-button', addUrl: '/client/addtowatched?tid=', removeUrl: '/client/removefromwatched?tid=' },
        { id: 'watchlist-button', addUrl: '/client/addtowatchlist/', removeUrl: '/client/removefromwatchlist?tid=' }
    ];

    const modal = document.getElementById('review-modal');
    const closeBtn = document.querySelector('.close-btn');
    const cancelBtn = document.getElementById('cancel');
    const saveBtn = document.getElementById('save');
    const reviewTextarea = document.getElementById('review-area');
    const ratingInput = document.getElementById('rating-area');

    if (modal) {
        modal.style.display = 'none';
    }

    buttons.forEach(btnConfig => {
        const btn = document.getElementById(btnConfig.id);
        if (!btn) return;
        const icon = btn.querySelector('i');
        icon.style.color = btn.dataset.state === 'active' ? '#710a42' : 'white';

        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            const tid = btn.dataset.tid;
            const state = btn.dataset.state;
            const url = state === 'inactive'
                ? btnConfig.addUrl + encodeURIComponent(tid)
                : btnConfig.removeUrl + encodeURIComponent(tid);
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

    const closeModal = () => {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
        if (reviewTextarea) reviewTextarea.value = '';
        if (ratingInput) ratingInput.value = '';
    };

    closeBtn?.addEventListener('click', closeModal);
    cancelBtn?.addEventListener('click', closeModal);

    window.addEventListener('click', (e) => {
        if (e.target === modal) closeModal();
    });

    saveBtn?.addEventListener('click', async () => {
        const reviewText = reviewTextarea?.value.trim();
        const rating = ratingInput?.value.trim();
        const watchedBtn = document.getElementById('watched-button');
        const movieTid = watchedBtn?.dataset.tid;

        if (!reviewText && !rating) {
            closeModal();
            const icon = watchedBtn.querySelector('i');
            icon.style.color = '#710a42';
            return;
        }

        try {
            const response = await fetch(`/client/addreview/${encodeURIComponent(movieTid)}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    comment: reviewText,
                    rating: rating ? parseFloat(rating) : null
                })
            });

            if (!response.ok) {
                throw new Error('Failed to save review');
            }

            console.log('Review saved successfully');
            closeModal();
            const icon = watchedBtn.querySelector('i');
            icon.style.color = '#710a42';
            location.reload();

        } catch (err) {
            console.error('Error saving review:', err);
            alert('Failed to save review. Please try again.');
        }
    });
});