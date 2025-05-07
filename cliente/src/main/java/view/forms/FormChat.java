package view.forms;

import net.miginfocom.swing.MigLayout;
import view.component.chat.Chat_Body;
import view.component.chat.Chat_Bottom;
import view.component.chat.Chat_Title;
import view.system.Form;

public class FormChat extends Form {
    private Chat_Title chatTitle;
    private Chat_Body chatBody;
    private Chat_Bottom chatBottom;

    public FormChat() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx", "0[fill]0", "0[]0[fill, grow]0[shrink 0]0"));
        chatTitle = new Chat_Title();
        chatBody = new Chat_Body();
        chatBottom = new Chat_Bottom();
        add(chatTitle, "wrap");
        add(chatBody, "growy, wrap");
        add(chatBottom, "h ::50%");
    }

    public Chat_Title getChatTitle() {
        return chatTitle;
    }

    public Chat_Body getChatBody() {
        return chatBody;
    }

    public Chat_Bottom getChatBottom() {
        return chatBottom;
    }

}
