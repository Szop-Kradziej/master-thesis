import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import AddIcon from "@material-ui/icons/AddCircle";
import InputWrapper from "./InputWrapper";

class AddNewItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.info} onClick={this.props.addActionHandler}>
                    <AddIcon/>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default AddNewItemComponent;
