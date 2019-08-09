import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DeleteIcon from "@material-ui/icons/DeleteForever";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class EditItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                <IconButton aria-label={this.props.info}
                            onClick={this.props.deleteActionHandler}>
                    <Tooltip title={this.props.info}>
                        <DeleteIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default EditItemComponent;
