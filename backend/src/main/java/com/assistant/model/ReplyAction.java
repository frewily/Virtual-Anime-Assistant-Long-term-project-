package com.assistant.model;

public class ReplyAction {
    private String text;
    private String expression;
    private String motion;
    private boolean sendToQQ;
    private boolean sendToDesktop;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getMotion() {
        return motion;
    }

    public void setMotion(String motion) {
        this.motion = motion;
    }

    public boolean isSendToQQ() {
        return sendToQQ;
    }

    public void setSendToQQ(boolean sendToQQ) {
        this.sendToQQ = sendToQQ;
    }

    public boolean isSendToDesktop() {
        return sendToDesktop;
    }

    public void setSendToDesktop(boolean sendToDesktop) {
        this.sendToDesktop = sendToDesktop;
    }
}
