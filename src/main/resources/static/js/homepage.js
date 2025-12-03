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

const loginButton=document.querySelector(".login-button");
const registerButton=document.querySelector(".register-button");
loginButton.addEventListener("click", (e)=>{
    window.location.href='/login';
})
registerButton.addEventListener("click", (e)=>{
    window.location.href='/signup';
})