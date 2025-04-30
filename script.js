const firebaseConfig = {
    apiKey: "AIzaSyAoZ9mVXvJ1df9f8RDsJOf-zPrqz78GUO0",
    authDomain: "gory-e69bb:firebaseapp.com",
    projectId: "gory-e69bb",
    storageBucket: "gory-e69bb.appspot.com",
    messagingSenderId: "213735469595",
    appId: "1:213735469595:web:3e59604b77df95b432febe"
}

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

document.getElementById("subscribe").addEventListener("click", async () => {
    try {
        const permission = await Notification.requestPermission();
        if (permission !== "granted") {
            alert("푸시 알림 권한이 거부되었습니다.");
            return;
        }

        const token = await messaging.getToken({
            vapidKey: "BI7kAHO3-M8uZ40xcW3fOd16wdn6fZCAVKO3h2eYo3um9FZgbhL-iJ6RHARxew3p-Nu-NSnugbzlxNLBtpgPPFk"
        });

        console.log("FCM Token:", token);
        alert("푸시 알림 구독 성공!");

        await fetch("http://localhost:3434/api/push/subscribe", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ token })
        });

    } catch (error) {
        console.error("푸시 알림 구독 실패:", error);
    }
});

document.getElementById("unsubscribe").addEventListener("click", async () => {
    try {
        const token = await messaging.getToken();
        if (!token) {
            alert("구독된 푸시 알림이 없습니다.");
            return;
        }

        await messaging.deleteToken();
        alert("푸시 알림 구독 취소됨");

        await fetch("http://localhost:3434/api/push/unsubscribe", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ token })
        });

    } catch (error) {
        console.error("푸시 알림 구독 취소 실패:", error);
    }
});

function displayNotification(title, body) {
    const notificationList = document.getElementById("notifications");
    const listItem = document.createElement("li");
    listItem.innerHTML = `<strong>${title}</strong>: ${body}`;
    notificationList.appendChild(listItem);
}

messaging.onMessage((payload) => {
    console.log("푸시 알림 수신:", payload);

    new Notification(payload.notification.title, {
        body: payload.notification.body,
        icon: "/logo.png"
    });

    displayNotification(payload.notification.title, payload.notification.body);
});

if ("serviceWorker" in navigator) {
    navigator.serviceWorker.register("firebase-messaging-sw.js")
        .then((registration) => {
            console.log("ervice Worker 등록 성공:", registration);
        })
        .catch((error) => {
            console.error("Service Worker 등록 실패:", error);
        });
}
