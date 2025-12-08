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
const followButton = document.querySelector(".follow-button");

if (followButton) {
    const profileUsername = document.querySelector('.username').textContent.trim();
    const isFollowing = followButton.textContent.trim() === 'Following';

    followButton.addEventListener("click", async (e) => {
        e.preventDefault();

        try {
            const response = await fetch(`/client/followorunfollow/${profileUsername}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Failed to update follow status');
            }
            if (followButton.textContent.trim() === 'Following') {
                followButton.textContent = 'Follow';
                followButton.style.background = 'linear-gradient(135deg, #0f0f1e 0%, #1a1a2e 100%)';
                followButton.style.borderColor = '#710a42';
                const followerCount = document.querySelector('.follow-number');
                if (followerCount) {
                    followerCount.textContent = parseInt(followerCount.textContent) - 1;
                }
            } else {
                followButton.textContent = 'Following';
                followButton.style.background = '#710a42';
                followButton.style.borderColor = '#710a42';
                followButton.style.color = 'white';
                const followerCount = document.querySelector('.follow-number');
                if (followerCount) {
                    followerCount.textContent = parseInt(followerCount.textContent) + 1;
                }
            }

        } catch (error) {
            console.error('Error updating follow status:', error);
            alert('Failed to update follow status. Please try again.');
        }
    });
}
function openEditModal() {
    const modal = document.getElementById('edit-modal');
    modal.style.display = 'block';
    const bioText = document.getElementById('bioText');
    const locationText = document.getElementById('locationText');

    if (bioText) document.getElementById('bioInput').value = bioText.textContent.trim();

    if (locationText) {
        const locationParts = locationText.textContent.trim().split(', ');
        document.getElementById('cityInput').value = locationParts[0] || '';
        document.getElementById('countryInput').value = locationParts[1] || '';
    }
}

function closeEditModal() {
    document.getElementById('edit-modal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('edit-modal');
    if (event.target === modal) {
        closeEditModal();
    }
}

async function saveProfile() {
    const bio = document.getElementById('bioInput').value.trim();
    const city = document.getElementById('cityInput').value.trim();
    const country = document.getElementById('countryInput').value.trim();
    const profilePic = document.getElementById('profilePicInput').value.trim();

    try {
        await fetch('/client/addbio', {
            method: 'POST',
            headers: { 'Content-Type': 'text/plain' },
            body: bio
        });
        const bioEl = document.getElementById('bioText');
        bioEl.textContent = bio || 'Always in need of something to watch.';

        if (!bio) {
            bioEl.style.fontStyle = 'italic';
            bioEl.style.opacity = '0.6';
        } else {
            bioEl.style.fontStyle = 'normal';
            bioEl.style.opacity = '1';
        }
        await fetch('/client/addcity', {
            method: 'POST',
            headers: { 'Content-Type': 'text/plain' },
            body: city
        });
        await fetch('/client/addcountry', {
            method: 'POST',
            headers: { 'Content-Type': 'text/plain' },
            body: country
        });
        const locationEl = document.getElementById('locationText');
        const locationIcon = locationEl?.parentElement;

        if (city && country) {
            locationEl.textContent = `${city}, ${country}`;
            if (locationIcon) locationIcon.style.display = 'flex';
        } else if (city) {
            locationEl.textContent = city;
            if (locationIcon) locationIcon.style.display = 'flex';
        } else if (country) {
            locationEl.textContent = country;
            if (locationIcon) locationIcon.style.display = 'flex';
        } else {
            if (locationIcon) locationIcon.style.display = 'none';
        }
        if (profilePic) {
            await fetch('/client/addpicture', {
                method: 'POST',
                headers: { 'Content-Type': 'text/plain' },
                body: profilePic
            });

            const imgEl = document.getElementById('profilePicture');
            imgEl.src = profilePic.startsWith('http')
                ? profilePic
                : 'https://image.tmdb.org/t/p/w780' + profilePic;
        }

        closeEditModal();

    } catch (error) {
        console.error('Error updating profile:', error);
        alert('Failed to update profile. Please try again.');
    }
}


window.selectedTop3Place = 1;
const firstPlace = document.getElementById('1st');
const secondPlace= document.getElementById('2nd');
const thirdPlace = document.getElementById('3rd');

if (firstPlace) {
    firstPlace.addEventListener("click", (e) => {
        window.selectedTop3Place = 1;
        openTop3Modal(1);
    });
}

if (secondPlace) {
    secondPlace.addEventListener("click", (e) => {
        window.selectedTop3Place = 2;
        openTop3Modal(2);
    });
}

if (thirdPlace) {
    thirdPlace.addEventListener("click", (e) => {
        window.selectedTop3Place = 3;
        openTop3Modal(3);
    });
}

function openTop3Modal(place) {
    const modal = document.getElementById("top3-modal");
    const placeText = document.getElementById("selectedPlace");

    if (placeText) {
        placeText.textContent = getOrdinalText(place);
    }

    modal.style.display = 'flex';
}

const moviePosters = document.querySelectorAll('.movie-grid .movie-poster');
function setPosterActive(e){
    const activePoster=e.currentTarget;
    moviePosters.forEach(poster => {
        poster.classList.remove('active');
    })
    activePoster.classList.add('active');
}
moviePosters.forEach(poster => {
    poster.addEventListener('click', setPosterActive);
})

function closeTop3Modal(){
    document.getElementById('top3-modal').style.display = 'none';
    const moviePosters=document.querySelectorAll('.movie-grid .movie-poster');
    moviePosters.forEach(poster =>{
        poster.classList.remove('active');
    })
}
async function saveTop3(){
    const activePoster = document.querySelector('.movie-grid .movie-poster.active');
    if(!activePoster){
       return;
    }
    const movieTid=activePoster.dataset.tid;
    const place = window.selectedTop3Place;

    try{
        const response = await fetch(`/client/top3/${place}/${movieTid}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            throw new Error('Failed to save top 3 movie');
        }
        const placeElement = document.getElementById(`${place}${getOrdinalSuffix(place)}`);
        if(placeElement) {
            placeElement.src = activePoster.src;
        }
        closeTop3Modal();
    }catch (error) {
        throw new Error('Failed to save top 3 movie');
    }
}
function getOrdinalSuffix(num) {
    if (num === 1) return 'st';
    if (num === 2) return 'nd';
    if (num === 3) return 'rd';
    return 'th';
}
function getOrdinalText(num) {
    return num + getOrdinalSuffix(num);
}
window.addEventListener('click', (e) => {
    const modal = document.getElementById('top3-modal');
    if (e.target === modal) {
        closeTop3Modal();
    }
});
