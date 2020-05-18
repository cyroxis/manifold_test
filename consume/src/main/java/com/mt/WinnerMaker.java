package com.mt;

import com.at.TypeCreator;

public class WinnerMaker implements TypeCreator {

    @Override
    public String getName(String baseTypeFqn) {
        return baseTypeFqn + "Winner";
    }
}
