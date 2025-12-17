
// get media devices
const openMediaDevices = async (constraints) => {
    return await navigator.mediaDevices.getUserMedia(constraints);
}

// signaling server connection
let conn = new WebSocket('ws://localhost:8080/socket');

// data chanel
let dataChannel = null;

// get local stream and add tracks to peer connection
const localStream = await openMediaDevices({video: true, audio: true});

// local video element
const remoteVideo = document.getElementById('localVideo');

// common RTC configuration
const configuration = {
    'iceServers': [
        {'urls': 'stun:stun.l.google.com:19302'}
    ]
};

// create peer connection
const peerConnection = new RTCPeerConnection(configuration);

// add local stream tracks to peer connection
localStream.getTracks().forEach(track => {
    peerConnection.addTrack(track, localStream);
});


// handle remote stream
peerConnection.addEventListener('track', async (event) => {
    const [remoteStream] = event.streams;
    remoteVideo.srcObject = remoteStream;
});

// handle incoming data channel
peerConnection.addEventListener('datachannel', event => {
    dataChannel = event.channel;
});

// handle new ice candidate
peerConnection.addEventListener('icecandidate', event => {
    if (event.candidate) {
        conn.send(JSON.stringify({'new-ice-candidate': event.candidate}));
    }
});

// handle new message
conn.onmessage(async (message) => {
    message = JSON.parse(message.data);
    if(message.iceCandidate) {
        try {
            await peerConnection.addIceCandidate(message.iceCandidate);
        } catch (e) {
            console.error('Error adding received ice candidate', e);
        }
    }
    else if(message.answer) {
        const remoteDesc = new RTCSessionDescription(message.answer);
        await peerConnection.setRemoteDescription(remoteDesc);
    }else if(message.offer){
        await answerOffer(message.offer);
    }
});


// handle connection state change
peerConnection.addEventListener('connectionstatechange', async event => {
    if (peerConnection.connectionState === 'connected') {

        // add remote stream to video element

    }
});


// create offer
async function creatOffer(){
    dataChannel = peerConnection.createDataChannel();
    const offer = await peerConnection.createOffer();
    await peerConnection.setLocalDescription(offer);
    conn.send(JSON.stringify({'offer': offer}) );
}

// answer offer
async function answerOffer(offer){
    await peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
    const answer = await peerConnection.createAnswer();
    await peerConnection.setLocalDescription(answer);
    conn.send(JSON.stringify({'answer': answer}) )
};






