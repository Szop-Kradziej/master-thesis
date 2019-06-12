import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import UploadIcon from "@material-ui/icons/CloudUpload";
import DownloadIcon from "@material-ui/icons/CloudDownload";
import InputWrapper from "./InputWrapper";

class UploadAndDownloadItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.uploadInfo}
                            onClick={this.props.uploadActionHandler}>
                    <UploadIcon/>
                </IconButton>
                <IconButton aria-label={this.props.downloadInfo}
                            disabled={this.props.downloadDisabled}
                            onClick={this.props.downloadActionHandler}>
                    <DownloadIcon/>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default UploadAndDownloadItemComponent;
