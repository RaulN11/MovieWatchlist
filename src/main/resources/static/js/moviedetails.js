
document.addEventListener('DOMContentLoaded', () => {

    const buttons = [
        {id: 'like-button', addUrl: '/client/addtoliked/', removeUrl: '/client/removeliked/'},
        {id: 'watched-button', addUrl: '/client/addtowatched?title=', removeUrl: '/client/removewatched?title='},
        {id: 'watchlist-button', addUrl: '/client/addtowatchlist/', removeUrl: '/client/removewatchlist/'}
    ];

    buttons.forEach(btnConfig => {
        const btn = document.getElementById(btnConfig.id);
        if (!btn) return;

        const icon = btn.querySelector('i');

        // Set initial color based on data-state
        if (btn.dataset.state === 'active') {
            icon.style.color = 'white';
        } else {
            icon.style.color = '#710a42';
        }

        btn.addEventListener('click', () => {
            const title = btn.dataset.title;
            const state = btn.dataset.state;

            // Determine URL based on current state
            const url = state === 'inactive' ? btnConfig.addUrl + encodeURIComponent(title)
                : btnConfig.removeUrl + encodeURIComponent(title);

            fetch(url, { method: 'POST', headers: {'Content-Type':'application/json'} })
                .then(response => response.json())
                .then(data => {
                    // Toggle state and button color
                    if (state === 'inactive') {
                        btn.dataset.state = 'active';
                        icon.style.color = 'white';
                    } else {
                        btn.dataset.state = 'inactive';
                        icon.style.color = '#710a42';
                    }
                })
                .catch(err => console.error(err));
        });
    });
});
