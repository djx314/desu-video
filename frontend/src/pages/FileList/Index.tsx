/** @jsx jsx */
import { jsx } from "@emotion/core"
import { useState } from "react"
import * as style from "./style"
import { getBarHeight, HeadBar } from "./Components/HeadBar"
import { FileListContent } from "./Components/FileListContent"
import axios from "axios"

const getDocumentHeight = () => document.documentElement.clientHeight

const FileList = () => {
    const [docHeight, docHeightState] = useState(getDocumentHeight())

    window.addEventListener("resize", () => {
        docHeightState(getDocumentHeight())
    })
    
    const [fileListModel, fileListModelState] = useState(Array<String>())

    axios.get<Array<String>>('/fileList')
        .then(function (response) {
            // handle success
            fileListModelState(response.data);
        })
        .catch(function (error) {
            // handle error
            console.log(error);
        })
        .then(function () {
            // always executed
        })

    return (
        <div css={style.bodyContent(docHeight)}>
            <HeadBar contentHeight={docHeight} />
            <FileListContent listHeight={docHeight - getBarHeight(docHeight)} fileList={fileListModel} />
        </div>
    )
}

export default FileList