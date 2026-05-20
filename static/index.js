const fileInput = document.getElementById("file-input");
const dropzone = document.getElementById("dropzone");
const fileChosen = document.getElementById("file-chosen");
const filenameEl = document.getElementById("filename");
const submitBtn = document.getElementById("submit-btn");
const statusLoad = document.getElementById("status-loading");
const statusErr = document.getElementById("status-error");

function setFile(file) {
  if (!file) return;
  filenameEl.textContent = file.name;
  fileChosen.classList.add("visible");
  submitBtn.disabled = false;
  statusErr.classList.remove("visible");
}

fileInput.addEventListener("change", () => setFile(fileInput.files[0]));

dropzone.addEventListener("dragover", (e) => {
  e.preventDefault();
  dropzone.classList.add("drag-over");
});
dropzone.addEventListener("dragleave", () =>
  dropzone.classList.remove("drag-over"),
);
dropzone.addEventListener("drop", (e) => {
  e.preventDefault();
  dropzone.classList.remove("drag-over");
  const file = e.dataTransfer.files[0];
  if (file) {
    // Sync to the file input so the rest of the logic works
    const dt = new DataTransfer();
    dt.items.add(file);
    fileInput.files = dt.files;
    setFile(file);
  }
});

submitBtn.addEventListener("click", async () => {
  const file = fileInput.files[0];
  if (!file) return;

  submitBtn.disabled = true;
  statusLoad.classList.add("visible");
  statusErr.classList.remove("visible");

  try {
    const body = new FormData();
    body.append("image_file", file);

    const res = await fetch("/image", { method: "POST", body });

    if (!res.ok) {
      const msg = await res.text().catch(() => res.statusText);
      throw new Error(`Server error ${res.status}: ${msg}`);
    }

    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "Masterpiece.xlsx";
    a.click();
    URL.revokeObjectURL(url);
  } catch (err) {
    statusErr.textContent = err.message;
    statusErr.classList.add("visible");
  } finally {
    statusLoad.classList.remove("visible");
    submitBtn.disabled = false;
  }
});
