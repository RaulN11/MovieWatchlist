document.addEventListener('DOMContentLoaded', () => {

    const buttons = [
        { id: 'like-button', addUrl: '/client/addtoliked/', removeUrl: '/client/removefromliked/' },
        { id: 'watched-button', addUrl: '/client/addtowatched?title=', removeUrl: '/client/removefromwatched/' },
        { id: 'watchlist-button', addUrl: '/client/addtowatchlist/', removeUrl: '/client/removefromwatchlist/' }
    ];

    buttons.forEach(btnConfig => {
        const btn = document.getElementById(btnConfig.id);
        if (!btn) return;

        const icon = btn.querySelector('i');
        if (btn.dataset.state === 'active') {
            icon.style.color = '#710a42';
        } else {
            icon.style.color = 'white';
        }

        btn.addEventListener('click', () => {
            const title = btn.dataset.title;
            const state = btn.dataset.state;

            // Determine URL and HTTP method
            const url = state === 'inactive' ? btnConfig.addUrl + encodeURIComponent(title)
                : btnConfig.removeUrl + encodeURIComponent(title);
            const method = state === 'inactive' ? 'POST' : 'DELETE';

            fetch(url, { method: method, headers: { 'Content-Type': 'application/json' } })
                .then(response => {
                    if (!response.ok) throw new Error('Network response was not ok');
                    return response.json();
                })
                .then(data => {
                    // Toggle state and button color
                    if (state === 'inactive') {
                        btn.dataset.state = 'active';
                        icon.style.color = '#710a42';
                    } else {
                        btn.dataset.state = 'inactive';
                        icon.style.color = 'white';
                    }
                })
                .catch(err => console.error(err));
        });
    });
});
