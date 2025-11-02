const searchButton = document.querySelector(".search-button");

searchButton.addEventListener("click", (e) => {
    e.preventDefault();
    let query = document.querySelector(".top-nav").value.trim();
    console.log(query);
    if (query) {
        window.location.href = `/searchMenu/movies/${encodeURIComponent(query)}`;
    }
});