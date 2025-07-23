console.log("JavaScript loaded and executing.");
document.addEventListener('DOMContentLoaded', function() {
    const stars = document.querySelectorAll('#star-rating .star');
    const ratingInput = document.getElementById('rating');
    let currentRating = parseInt(ratingInput.value);

    // 별 시각적 상태 업데이트 함수
    function updateStarVisuals(value) {
        console.log(`updateStarVisuals called with value: ${value}`);
        stars.forEach(star => {
            const starValue = parseInt(star.dataset.value);
            if (starValue <= value) {
                star.classList.add('selected');
                console.log(`Star ${starValue} added 'selected'`);
            } else {
                star.classList.remove('selected');
                console.log(`Star ${starValue} removed 'selected'`);
            }
        });
    }

    stars.forEach(star => {
        star.addEventListener('click', function() {
            const value = parseInt(this.dataset.value);
            ratingInput.value = value;
            currentRating = value; // 현재 선택된 평점 업데이트
            console.log(`Clicked star value: ${value}, currentRating: ${currentRating}`);
            updateStarVisuals(value); // 클릭 시 selected 클래스 적용
        });

        star.addEventListener('mouseover', function() {
            const value = parseInt(this.dataset.value);
            console.log(`Mouseover star value: ${value}`);
            updateStarVisuals(value); // 마우스 오버 시 별 채우기
        });

        star.addEventListener('mouseout', function() {
            console.log(`Mouseout, restoring to currentRating: ${currentRating}`);
            updateStarVisuals(currentRating); // 마우스 아웃 시 현재 선택된 평점으로 복원
        });
    });

    // 초기 로드 시 별 상태 업데이트 (기본값 0이므로 모두 빈 별)
    console.log(`Initial load, currentRating: ${currentRating}`);
    updateStarVisuals(currentRating);
});