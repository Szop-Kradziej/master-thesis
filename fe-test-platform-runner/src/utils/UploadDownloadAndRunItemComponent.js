import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import UploadIcon from "@material-ui/icons/CloudUpload";
import DownloadIcon from "@material-ui/icons/CloudDownload";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";
import RunIcon from "@material-ui/icons/PlayArrow";

class UploadDownloadAndRunItemComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {disabled: false};
    }

    handleRunTests = () => {
        this.setState({disabled: true});
        this.props.runActionHandler()
            .then(this.handleRunFinished)
            .catch(this.handleRunFinished)
    };

    handleRunFinished = () => {
        this.setState({disabled: false});
    };

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.uploadInfo}
                            onClick={this.props.uploadActionHandler}>
                    <Tooltip title={this.props.uploadInfo}>
                        <UploadIcon/>
                    </Tooltip>
                </IconButton>
                <IconButton aria-label={this.props.downloadInfo}
                            disabled={this.props.disabled}
                            onClick={this.props.downloadActionHandler}>
                    <Tooltip title={this.props.downloadInfo}>
                        <DownloadIcon/>
                    </Tooltip>
                </IconButton>
                <IconButton aria-label={this.props.runInfo}
                            disabled={this.props.disabled || this.state.disabled}
                            onClick={this.handleRunTests}>
                    <Tooltip title={this.props.runInfo}>
                        <RunIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default UploadDownloadAndRunItemComponent;
