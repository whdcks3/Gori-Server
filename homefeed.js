const API_URL = "http://localhost:3434/api/feed/home"; 
let currentPage = 0;
let hasNext = false;

async function fetchFeeds(category, page) {
    try {
        const response = await fetch(`${API_URL}?category=${category}&page=${page}`, {
            method: "GET",
            headers: {
                "Authorization" : "Bearer eyJhbGciOiJSUzI1NiJ9.eyJyb2xlcyI6WyJVU0VSIl0sInN1YiI6InNqMDEyOTQ0QGdtYWlsLmNvbSIsImlhdCI6MTc0MTA4ODI3OCwiZXhwIjoxNzQxMDkxODc4fQ.u2mhO620924FSpwq_1XBvMQgBNojBZRugaFugGaBm0D41Ik28Cs5Dz17iOGPTtlCjzVKako2Ve8STrY2QAcH7R1PGLjGZR82qBorqEMmD0bGXKCbmBKBDAsShw6KUj4SNM86EsjUiu7kuSmm0QP6WhGvf2qZ2A2Ft8489wNpQw0GhYJWKLQuaK8i8YEIbXIw6yyh2U2g-aj7eUmSHmSJ53nJqFtE9psudyP1oXb0c7xJVeKvr6fXsQu9QyVxBYaKwOvuBIv-DArQXSsVrQDhB1IMQCrHluZPAfW6ObH_5S9_qb6YFxS6J4E239Iv_sa7uaZiSAH57S-biQdmdZFrYA",
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) throw new Error("서버 오류: " + response.status);

        const data = await response.json();
        updateFeedList(data.list);
        hasNext = data.hasNext;

        document.getElementById("loadMore").style.display = hasNext ? "block" : "none";

    } catch (error) {
        console.error("피드 불러오기 실패:", error);
    }
}

function updateFeedList(feeds) {
    const feedList = document.getElementById("feedList");
    if (currentPage === 0) feedList.innerHTML = "";

    feeds.forEach(feed => {
        const listItem = document.createElement("li");
        listItem.classList.add("feed-item");

        let imageHTML = "";
        if (feed.images.length > 0) {
            imageHTML = feed.images.map(imgUrl => `<img src="${imgUrl}" class="feed-image">`).join("");
        }

        listItem.innerHTML = `
            <h3>${feed.nickname} | ${feed.category}</h3>
            <p>${feed.content}</p>
            ${imageHTML}
            <div class="feed-footer">
                <span class="feed-date">${feed.datetime}</span>
                <button class="like-btn" data-liked="${feed.like}">${feed.like ? "❤️ 좋아요" : "🤍 좋아요"}</button>
            </div>
        `;

        const likeBtn = listItem.querySelector(".like-btn");
        likeBtn.addEventListener("click", () => toggleLike(likeBtn));

        feedList.appendChild(listItem);
    });
}

function toggleLike(button) {
    const isLiked = button.getAttribute("data-liked") === "true";
    button.innerHTML = isLiked ? "🤍 좋아요" : "❤️ 좋아요";
    button.setAttribute("data-liked", isLiked ? "false" : "true");
}

document.getElementById("loadFeeds").addEventListener("click", () => {
    const category = document.getElementById("category").value;
    currentPage = 0;
    fetchFeeds(category, currentPage);
});

document.getElementById("loadMore").addEventListener("click", () => {
    currentPage++;
    const category = document.getElementById("category").value;
    fetchFeeds(category, currentPage);
});
