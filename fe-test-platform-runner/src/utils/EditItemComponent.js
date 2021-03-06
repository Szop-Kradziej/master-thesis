import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import EditIcon from "@material-ui/icons/Edit";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class EditItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.info}
                            onClick={this.props.editActionHandler}>
                    <Tooltip title={this.props.info}>
                        <EditIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default EditItemComponent;
