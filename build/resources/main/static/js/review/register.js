document.addEventListener('DOMContentLoaded', function() {
    console.log("JavaScript loaded and executing.");
    const stars = document.querySelectorAll('#star-rating .star');
    const ratingInput = document.getElementById('rating');
    let currentRating = parseInt(ratingInput.value);

    function updateStarVisuals(value) {
        stars.forEach(star => {
            const starValue = parseInt(star.dataset.value);
            if (starValue <= value) {
                star.classList.add('selected');
            } else {
                star.classList.remove('selected');
            }
        });
    }

    stars.forEach(star => {
        star.addEventListener('click', function() {
            const value = parseInt(this.dataset.value);
            ratingInput.value = value;
            currentRating = value;
            updateStarVisuals(value);
        });

        star.addEventListener('mouseover', function() {
            const value = parseInt(this.dataset.value);
            updateStarVisuals(value);
        });

        star.addEventListener('mouseout', function() {
            updateStarVisuals(currentRating);
        });
    });

    updateStarVisuals(currentRating);
});