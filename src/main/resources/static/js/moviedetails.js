document.addEventListener('DOMContentLoaded', () => {
    const searchButton = document.querySelector(".search-button");

    searchButton.addEventListener("click", (e) => {
        e.preventDefault();
        let query = document.querySelector(".top-nav").value.trim();
        console.log(query);
        if (query) {
            window.location.href = `/searchMenu/movies/${encodeURIComponent(query)}`;
        }
    });

    const buttons = [
        { id: 'like-button', addUrl: '/client/addtoliked/', removeUrl: '/client/removefromliked/' },
        { id: 'watched-button', addUrl: '/client/addtowatched?tid=', removeUrl: '/client/removefromwatched/' },
        { id: 'watchlist-button', addUrl: '/client/addtowatchlist/', removeUrl: '/client/removefromwatchlist/' }
    ];

    const modal = document.getElementById('review-modal');
    const closeBtn = document.querySelector('.close-btn');
    const cancelBtn = document.getElementById('cancel');
    const saveBtn = document.getElementById('save');
    const reviewTextarea = document.getElementById('review-area');
    const ratingInput = document.getElementById('rating-area');

    // Initialize modal as hidden
    if (modal) {
        modal.style.display = 'none';
    }

    buttons.forEach(btnConfig => {
        const btn = document.getElementById(btnConfig.id);
        if (!btn) return;

        const icon = btn.querySelector('i');
        // Set initial color based on state
        icon.style.color = btn.dataset.state === 'active' ? '#710a42' : 'white';

        btn.addEventListener('click', async (e) => {
            e.preventDefault();

            const tid = btn.dataset.tid;
            const currentState = btn.dataset.state;
            const isActivating = currentState === 'inactive';

            // For watched button being activated, show modal FIRST
            if (btnConfig.id === 'watched-button' && isActivating) {
                modal.style.display = 'flex';
                document.body.style.overflow = 'hidden';
                // Don't make the API call yet - wait for modal save
                return;
            }

            // For all other cases, make the API call
            const url = isActivating
                ? btnConfig.addUrl + encodeURIComponent(tid)
                : btnConfig.removeUrl + encodeURIComponent(tid);
            const method = isActivating ? 'POST' : 'DELETE';

            try {
                const response = await fetch(url, {
                    method,
                    headers: { 'Content-Type': 'application/json' }
                });

                if (!response.ok) throw new Error('Network error');

                // Update state and color immediately after successful response
                if (isActivating) {
                    btn.dataset.state = 'active';
                    icon.style.color = '#710a42';
                } else {
                    btn.dataset.state = 'inactive';
                    icon.style.color = 'white';
                }

            } catch (err) {
                console.error('Error:', err);
                alert('An error occurred. Please try again.');
            }
        });
    });

    const closeModal = () => {
        if (modal) {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
        }
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
        const icon = watchedBtn?.querySelector('i');

        // First, add to watched list
        try {
            const watchedResponse = await fetch(`/client/addtowatched?tid=${encodeURIComponent(movieTid)}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });

            if (!watchedResponse.ok) {
                throw new Error('Failed to add to watched list');
            }

            // Update button state
            watchedBtn.dataset.state = 'active';
            if (icon) icon.style.color = '#710a42';

            // If there's a review, save it
            if (reviewText || rating) {
                const reviewResponse = await fetch(`/client/addreview/${encodeURIComponent(movieTid)}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        comment: reviewText,
                        rating: rating ? parseFloat(rating) : null
                    })
                });

                if (!reviewResponse.ok) {
                    throw new Error('Failed to save review');
                }

                console.log('Review saved successfully');
            }

            closeModal();
            location.reload();

        } catch (err) {
            console.error('Error:', err);
            alert('Failed to save. Please try again.');
        }
    });
});