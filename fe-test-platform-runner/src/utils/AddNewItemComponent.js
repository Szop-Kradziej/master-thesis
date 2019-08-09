import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import AddIcon from "@material-ui/icons/AddCircle";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class AddNewItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.info}
                            onClick={this.props.addActionHandler}>
                    <Tooltip title={this.props.info}>
                        <AddIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default AddNewItemComponent;
