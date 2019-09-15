import {withStyles} from "@material-ui/core";
import TableCell from "@material-ui/core/TableCell/TableCell";

export const CustomTableCell = withStyles(() => ({
    head: {
        color: "black",
        fontWeight: 700,
        fontSize: 26,
        border: 0
    },
    body: {
        color: "black",
        fontSize: 22,
        border: 0
    }
}))(TableCell);

export const CustomHeaderCell = withStyles(() => ({
    head: {
        color: "black",
        fontWeight: 700,
        fontSize: 30,
        border: 0
    },
    body: {
        color: "black",
        fontSize: 12,
        border: 0
    }
}))(TableCell);

export const styles = (theme) => ({
    app: {
        textAlign: "center",
        fontSize: 40,
        fontWeight: 700
    },
    table: {
        minWidth: 900,
        fontSize: 40,
        color: "black"
    },
    link: {
        textDecoration: "none"
    },
    paper: {
        position: 'absolute',
        width: theme.spacing.unit * 50,
        backgroundColor: theme.palette.background.paper,
        boxShadow: theme.shadows[5],
        padding: theme.spacing.unit * 4,
        outline: 'none',
    },
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        width: 200,
    },
    testCasesRow: {
        display: 'flex'
    },
    root: {
        width: '100%',
    },
    heading: {
        width: 1700,
    },
    headingMainPanel: {
        fontSize: 30,
        fontWeight: 700,
    },
    testLabel: {
        fontSize: 30,
        fontWeight: 700
    },
    testsHeading: {
        width: 1700,
        fontSize: 22,
    },
        taskName: {
        fontSize: 26,
        fontWeight: 700,
            width: 1700,
    },
    root_2: {
         display: "flex",
         width: 1700,
     }
});