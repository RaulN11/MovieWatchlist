const searchButton = document.querySelector(".search-button");
const loginButton=document.querySelector(".login-button");
const registerButton=document.querySelector(".register-button");
searchButton.addEventListener("click", (e) => {
    e.preventDefault();
    let query = document.querySelector(".top-nav").value.trim();
    console.log(query);
    if (query) {
        window.location.href = `/searchMenu/movies/${encodeURIComponent(query)}`;
    }
});
loginButton.addEventListener("click", (e)=>{
    window.location.href='/login';
})
registerButton.addEventListener("click", (e)=>{
    window.location.href='/signup';
})