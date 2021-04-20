    var chat = document.getElementById("messageList");

    function addMessage(message){
        var clone = document.getElementById("messageBox").content.cloneNode(true);
        var cssClass;

        if(message.local) {
            cssClass = "local";
        }
        else {
            cssClass = "remote";
        }

        clone.getElementById("messageElement").classList.add(cssClass);

        clone.getElementById("text").innerText = message.text;
        clone.getElementById("hour").innerText = message.hour;

        chat.append(clone);

        //window.scrollTo(0,document.body.scrollHeight);
        $("#messageList").animate({scrollTop: $('#messageList').prop("scrollHeight")}, 1000);
    }

    function clearChatHistory() {
        $("#messageList").empty();
    }

    function loadMessageHistory(messages) {
        clearChatHistory();

        for (i=0; i < messages.length; i++) {
            addMessage(messages[i]);
        }
    }