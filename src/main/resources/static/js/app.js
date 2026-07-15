document.addEventListener("DOMContentLoaded", () => {
    const memo = document.querySelector("#memo");
    const count = document.querySelector("#memo-count");

    if (!memo || !count) {
        return;
    }

    const updateCount = () => {
        count.textContent = memo.value.length.toLocaleString("ja-JP");
    };

    memo.addEventListener("input", updateCount);
    updateCount();
});
