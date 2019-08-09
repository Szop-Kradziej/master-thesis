import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import StatisticsIcon from "@material-ui/icons/Assessment";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class StatisticsItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.info}
                            disabled={this.props.disabled}
                            onClick={this.props.getStatisticsActionHandler}>
                    <Tooltip title={this.props.info}>
                        <StatisticsIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default StatisticsItemComponent;
