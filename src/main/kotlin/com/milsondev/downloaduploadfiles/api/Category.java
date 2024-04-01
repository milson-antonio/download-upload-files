package com.milsondev.downloaduploadfiles.api;

public enum Category {
    CV("CV"),
    PASSAPORT("Passaport"),
    LANGUAGE_CERTIFICATE("Language Certificate"),
    OTHER("Other"),
    LETTER_OF_MOTIVATION("Letter Of Motivation"),
    SCHOOL_CERTIFICATE("School Certificate");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Category fromString(String text) {
        if (text != null) {
            String trimmedText = text.trim().replace(" ", "");
            for (Category category : Category.values()) {
                String trimmedDescription = category.getDescription().trim().replace(" ", "");
                if (trimmedText.equalsIgnoreCase(trimmedDescription)) {
                    return category;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum enum com descrição correspondente a " + text);
    }
}

