const searchButton = document.querySelector(".search-button");
const searchInput = document.querySelector(".top-nav");
searchButton.addEventListener("click", (e) => {
    e.preventDefault();
    performSearch();
});
searchInput.addEventListener("keypress", (e)=>{
    if(e.key === "Enter") {
        e.preventDefault();
        performSearch();
    }
})
function performSearch(){
    let query = searchInput.value.trim();
    if(query){
        window.location.href= `/searchMenu/movies/${encodeURIComponent(query)}`;
    }
}
const setActiveButton = () => {
    const currentPath = window.location.pathname;
    const filterBtns = document.querySelectorAll('.filter-btn');
    const pathParts = currentPath.split('/');
    const currentFilter = pathParts[pathParts.length - 1] || 'all';

    filterBtns.forEach(btn => {
        const filterText = btn.textContent.trim().toLowerCase();
        let filterParam;

        switch(filterText) {
            case "all":
                filterParam = 'all';
                break;
            case 'liked':
                filterParam = 'liked';
                break;
            case 'highest rating':
                filterParam = 'highest';
                break;
            case 'lowest rating':
                filterParam = 'lowest';
                break;
            default:
                filterParam = 'all';
        }

        if (filterParam === currentFilter) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });
};

setActiveButton();

const filterBtns = document.querySelectorAll('.filter-btn');
filterBtns.forEach(btn => {
    btn.addEventListener('click', function() {
        const filterText = this.textContent.trim().toLowerCase();
        let filterParam;

        switch(filterText) {
            case "all":
                filterParam = 'all';
                break;
            case 'liked':
                filterParam = 'liked';
                break;
            case 'highest rating':
                filterParam = 'highest';
                break;
            case 'lowest rating':
                filterParam = 'lowest';
                break;
            default:
                filterParam = 'all';
        }

        window.location.href = `/watched/${filterParam}`;
    });
});