package org.fourthline.cling.binding.staging;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;

import java.util.ArrayList;
import java.util.List;


public class MutableAction {
    public List<MutableActionArgument> arguments = new ArrayList();
    public String name;

    public Action build() {
        return new Action(this.name, createActionArgumennts());
    }

    public ActionArgument[] createActionArgumennts() {
        ActionArgument[] actionArgumentArr = new ActionArgument[this.arguments.size()];
        int i = 0;
        for (MutableActionArgument mutableActionArgument : this.arguments) {
            actionArgumentArr[i] = mutableActionArgument.build();
            i++;
        }
        return actionArgumentArr;
    }
}
