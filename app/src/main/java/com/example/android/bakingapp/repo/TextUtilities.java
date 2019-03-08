package com.example.android.bakingapp.repo;

import com.example.android.bakingapp.model.Ingredient;

import java.util.List;

public final class TextUtilities {

    private TextUtilities(){

    }

    public static String processIngredients(List<Ingredient> ingredients) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            String ingredient = ingredients.get(i).getIngredient();
            String start = ingredient.substring(0, 1);
            String end = ingredient.substring(1, ingredient.length());
            sb.append("\u2022 ")
                    .append(start.toUpperCase())
                    .append(end)
                    .append(" - ");
            String quantity = String.valueOf(ingredients.get(i).getQuantity());
                if(quantity.substring(quantity.length()-2, quantity.length()).equals(".0")){
                    quantity = quantity.substring(0, quantity.length() - 2);
                }
             sb.append(quantity)
                        .append(" ");
            switch (ingredients.get(i).getMeasure()) {
                case "CUP":
                    sb.append("cup");
                    break;
                case "TBLSP":
                    sb.append("table spoon");
                    break;
                case "TSP":
                    sb.append("tea spoon");
                    break;
                case "K":
                    sb.append("kilo");
                    break;
                case "G":
                    sb.append("gram");
                    break;
                case "OZ":
                    sb.append("ounce");
                    break;
                case "UNIT":
                    break;
            }
            if(ingredients.get(i).getQuantity() > 1 && !ingredients.get(i).getMeasure().equals("UNIT")) sb.append("s");
            sb.append("\n");
        }
    return sb.toString();
    }
}
