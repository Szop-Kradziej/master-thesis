import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import UploadIcon from "@material-ui/icons/CloudUpload";
import InputWrapper from "./InputWrapper";
import AddIcon from "@material-ui/icons/AddCircle";

class AddAndUploadItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.addInfo} onClick={this.props.addActionHandler}>
                    <AddIcon/>
                </IconButton>
                <IconButton aria-label={this.props.uploadInfo}
                            onClick={this.props.uploadActionHandler}>
                    <UploadIcon/>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default AddAndUploadItemComponent;
