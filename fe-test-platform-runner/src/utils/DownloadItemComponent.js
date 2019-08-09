import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DownloadIcom from "@material-ui/icons/CloudDownload";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class DownloadItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.info}
                            disabled={this.props.disabled}
                            onClick={this.props.downloadActionHandler}>
                    <Tooltip title={this.props.info}>
                        <DownloadIcom/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default DownloadItemComponent;
