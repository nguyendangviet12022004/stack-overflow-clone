(async () => {

    // get media devices
    const openMediaDevices = async (constraints) => {
        return await navigator.mediaDevices.getUserMedia(constraints);
    }

    // pending ice candidates
    let pendingCandidates = [];

    // data chanel
    let dataChannel = null;

    // get local stream and add tracks to peer connection
    const localStream = await openMediaDevices({video: true, audio: true});

    // local video element
    const localVideo = document.getElementById('localVideo');
    localVideo.srcObject = localStream;

    // remote video element
    const remoteVideo = document.getElementById('remoteVideo');

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
        remoteVideo.srcObject = event.streams[0];
    });

    // handle incoming data channel
    peerConnection.addEventListener('datachannel', event => {
        dataChannel = event.channel;

        // on open data channel
        dataChannel.onopen = () => {
            console.log("Data channel is open");
        };


        // on close data channel
        dataChannel.onclose = () => {
            console.log("Data channel is closed");
        };


    });

    // handle new ice candidate
    peerConnection.addEventListener('icecandidate', event => {
        if (event.candidate) {
            conn.send(JSON.stringify({'iceCandidate': event.candidate}));
        }
    });

    // signaling server connection
    let conn = new WebSocket('ws://localhost:8080/ws/signaling');

    // handle new message
    conn.onmessage = async (message) => {
        console.log(message.data)
        message = JSON.parse(message.data);

        if (message.iceCandidate) {
            if (peerConnection.remoteDescription) {
                await peerConnection.addIceCandidate(message.iceCandidate);
            } else {
                pendingCandidates.push(message.iceCandidate);
            }
        }

        if (message.answer) {
            await peerConnection.setRemoteDescription(message.answer);
            pendingCandidates.forEach(c =>
                peerConnection.addIceCandidate(c)
            );
            pendingCandidates = [];
        }

        if(message.offer){
            await answerOffer(message.offer);
        }
    };

// create offer
    async function creatOffer(){
        console.log("offer")
        dataChannel = peerConnection.createDataChannel("chat");

        dataChannel.onopen = () => {
            console.log("Data channel open (caller)");
            // dataChannel.send("hello");
        };

        dataChannel.onclose = () => {
            console.log("Data channel closed (caller)");
        };
        const offer = await peerConnection.createOffer();
        await peerConnection.setLocalDescription(offer);
        conn.send(JSON.stringify({'offer': offer}) );
    }

// answer offer
    async function answerOffer(offer){
        console.log("answer offer")
        await peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
        const answer = await peerConnection.createAnswer();
        await peerConnection.setLocalDescription(answer);
        conn.send(JSON.stringify({'answer': answer}) )
    };


    const isCaller = location.hash === '#caller';


    conn.onopen = async () => {
        if (isCaller) {
            await creatOffer();
        }
    };

})()