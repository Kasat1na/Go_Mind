package com.example.gomind.model;

public class Question {
    private Long id;

    /**
     * Текст вопроса.
     */

    private String text;

    /**
     * Вариант ответа A.
     */

    private String optionA;

    /**
     * Вариант ответа B.
     */

    private String optionB;

    /**
     * Вариант ответа C.
     */

    private String optionC;
    /**
     * Вариант ответа D.
     */
    private String optionD;

    /**
     * Правильный ответ (например, "A", "B", "C", "D").
     */
    private String correctAnswer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}