window.addEventListener('pageshow', function (event) {
    if (event.persisted) {
        // 뒤로가기로 접근한 경우 새로고침
        window.location.reload();
    }
});