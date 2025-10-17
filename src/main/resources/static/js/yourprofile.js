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
        if (bio) {
            await fetch('/client/addbio', {
                method: 'POST',
                headers: { 'Content-Type': 'text/plain' },
                body: bio
            });
            document.getElementById('bioText').textContent = bio;
        }
        if (city) {
            await fetch('/client/addcity', {
                method: 'POST',
                headers: { 'Content-Type': 'text/plain' },
                body: city
            });
        }
        if (country) {
            await fetch('/client/addcountry', {
                method: 'POST',
                headers: { 'Content-Type': 'text/plain' },
                body: country
            });
        }
        const locationEl = document.getElementById('locationText');
        if (city && country) {
            locationEl.textContent = `${city}, ${country}`;
        } else if (city) {
            locationEl.textContent = city;
        } else if (country) {
            locationEl.textContent = country;
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
