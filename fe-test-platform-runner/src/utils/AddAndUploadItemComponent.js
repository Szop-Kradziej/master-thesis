import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import UploadIcon from "@material-ui/icons/CloudUpload";
import InputWrapper from "./InputWrapper";
import AddIcon from "@material-ui/icons/AddCircle";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class AddAndUploadItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.addInfo}
                            onClick={this.props.addActionHandler}>
                    <Tooltip title={this.props.addInfo}>
                        <AddIcon/>
                    </Tooltip>
                </IconButton>
                <IconButton aria-label={this.props.uploadInfo}
                            onClick={this.props.uploadActionHandler}>
                    <Tooltip title={this.props.uploadInfo}>
                        <UploadIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default AddAndUploadItemComponent;
