import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import RunIcon from "@material-ui/icons/PlayArrow";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class RunItemComponent extends Component {

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
                <IconButton aria-label={this.props.info}
                            disabled={this.props.disabled || this.state.disabled}
                            onClick={this.handleRunTests}>
                    <Tooltip title={this.props.info}>
                        <RunIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default RunItemComponent;
