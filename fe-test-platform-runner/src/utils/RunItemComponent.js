import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import RunIcon from "@material-ui/icons/PlayArrow";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class RunItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.info}
                            disabled={this.props.disabled}
                            onClick={this.props.runActionHandler}>
                    <Tooltip title={this.props.info}>
                        <RunIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default RunItemComponent;
