importScripts("https://www.gstatic.com/firebasejs/10.11.1/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/10.11.1/firebase-messaging-compat.js");

firebase.initializeApp({
    apiKey: "AIzaSyAoZ9mVXvJ1df9f8RDsJOf-zPrqz78GUO0",
    authDomain: "gory-e69bb:firebaseapp.com",
    projectId: "gory-e69bb",
    storageBucket: "gory-e69bb.appspot.com",
    messagingSenderId: "213735469595",
    appId: "1:213735469595:web:3e59604b77df95b432febe"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    console.log("백그라운드에서 푸시 알림 수신:", payload);
    self.registration.showNotification(payload.notification.title, {
        body: payload.notification.body,
        icon: "/logo.png"
    });
});