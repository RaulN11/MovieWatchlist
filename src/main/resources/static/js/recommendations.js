const searchButton = document.querySelector(".search-button");

searchButton.addEventListener("click", (e) => {
    e.preventDefault();
    let query = document.querySelector(".top-nav").value.trim();
    if (query) {
        window.location.href = `/searchMenu/movies/${encodeURIComponent(query)}`;
    }
});

async function generateRecommendations(){
    const loading = document.getElementById('loading');
    const emptyState = document.getElementById('emptyState');
    const recGrid = document.getElementById('recGrid');
    const generateBtn = document.querySelector('.generate-btn'); // Fixed: use querySelector with dot

    emptyState.style.display = 'none';
    loading.classList.add('active');
    recGrid.style.display = 'none';
    generateBtn.disabled = true;

    try{
        const response = await fetch('/client/api/recommendations');
        if(!response.ok){
            throw new Error("Failed to load recommendations");
        }
        const movies = await response.json();

        loading.classList.remove('active');
        recGrid.style.display = 'grid';
        generateBtn.disabled = false;
        recGrid.innerHTML = '';

        movies.forEach((movie, index) => {
            const matchPercentage = 95 - (index * 2);

            const card = document.createElement('div');
            card.className = 'rec-card';
            card.onclick = () => window.location.href = `/details/${movie.tid}`;
            card.innerHTML = `
                <img src="https://image.tmdb.org/t/p/w780${movie.posterPath}" 
                     alt="${movie.title}" 
                     class="rec-poster"
                     onerror="this.src='https://www.nicepng.com/png/detail/311-3111609_clapperboard-movie-icon.png'">
                <div class="rec-info">
                    <h3 class="rec-title">${movie.title}</h3>
                    <div class="rec-match">
                        <span>${matchPercentage}%</span>
                        <div class="match-bar">
                            <div class="match-fill" style="width: ${matchPercentage}%"></div>
                        </div>
                    </div>
                </div>
            `;
            recGrid.appendChild(card);
        });

        if(movies.length === 0){
            recGrid.style.display = 'none';
            emptyState.style.display = 'block';
            emptyState.querySelector('h3').textContent = 'No Recommendations Found';
            emptyState.querySelector('p').textContent = 'Try adding more movies to your top 3 favorites to get better recommendations.';
        }
    } catch (error){
        console.error('Error:', error); // Added error logging
        loading.classList.remove('active');
        generateBtn.disabled = false;
        emptyState.style.display = 'block';
        emptyState.querySelector('h3').textContent = 'Something Went Wrong';
        emptyState.querySelector('p').textContent = 'Failed to generate recommendations. Please try again later.';
    }
}