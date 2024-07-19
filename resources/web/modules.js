console.log('modules.js loaded');
loadModules('Combat')

// func for altmanager

let altsData = {}; // Object to store fetched alts data for easy access

let isHovered = false;
let hoverTimeout;

const port = 23333;

async function createAlt(Username, Accounttype) {
    const altcontainer = document.querySelector('.alt-display');

    const altcard = document.createElement('div');
    altcard.classList.add('alt');

    const playerhead = document.createElement('img');
    playerhead.classList.add('player-head');
    playerhead.src = `https://mc-heads.net/avatar/${Username}`;

    const playerinfo = document.createElement('div');
    playerinfo.classList.add('player-information');

    const playerName = document.createElement('div');
    playerName.classList.add('player-name');
    playerName.textContent = Username;

    const accountType = document.createElement('div');
    accountType.classList.add('account-type');
    accountType.textContent = Accounttype;

    const altButtons = document.createElement('div');
    altButtons.classList.add('alt-buttons');

    const loginButton = document.createElement('a');
    loginButton.setAttribute('href', '#');
    loginButton.textContent = 'Login';
    loginButton.dataset.username = Username; // Data attribute for identifying button
    loginButton.addEventListener('click', async (e) => {
        e.preventDefault();
        await loginAlt(Username, loginButton, altcard);
    });

    const removeButton = document.createElement('a');
    removeButton.setAttribute('href', '#');
    removeButton.textContent = 'Remove';
    removeButton.style.marginLeft = '10px';
    removeButton.addEventListener('click', async (e) => {
        e.preventDefault();
        removeButton.textContent = 'Deleting...';
        try {
            const response = await fetch(`http://localhost:${port}/api/DeleteAlt?altname=${Username}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            const data = await response.json();
            if (data.success) {
                altcard.remove();
            } else {
                removeButton.textContent = 'Remove Failed';
                console.error(data.reason);
                setTimeout(() => {
                    removeButton.textContent = 'Remove';
                }, 3000);
            }
        } catch (error) {
            console.error('Error deleting alt:', error);
            removeButton.textContent = 'Delete Error';
            setTimeout(() => {
                removeButton.textContent = 'Remove';
            }, 3000);
        }
    });

    altButtons.appendChild(loginButton);
    altButtons.appendChild(removeButton);

    playerinfo.appendChild(playerName);
    playerinfo.appendChild(accountType);
    playerinfo.appendChild(altButtons);

    altcard.appendChild(playerhead);
    altcard.appendChild(playerinfo);

    altcontainer.appendChild(altcard);
}

async function loginAlt(Username, buttonElement, altCardElement) {
    buttonElement.textContent = 'Logging in...';
    try {
        const response = await fetch(`http://localhost:${port}/api/AltLogin?altname=${Username}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        const data = await response.json();
        if (data.success) {
            buttonElement.textContent = 'Logged';
            altCardElement.style.border = '1px solid white';
        } else {
            buttonElement.textContent = 'Login Failed';
            console.error(data.reason);
            setTimeout(() => {
                buttonElement.textContent = 'Login';
            }, 3000);
        }
    } catch (error) {
        console.error('Error logging in:', error);
        buttonElement.textContent = 'Login Error';
        setTimeout(() => {
            buttonElement.textContent = 'Login';
        }, 3000);
    }
}

async function updateAlts() {
    try {
        const altcontainer = document.querySelector('.alt-display');
        altcontainer.innerHTML = ''; // Clear existing content
        const response = await fetch(`http://localhost:${port}/api/getAltAccounts`);
        const altdata = await response.json();
        altsData = altdata; // Store fetched data

        Object.keys(altdata).forEach(username => {
            const {username: Username, accounttype: Accounttype} = altdata[username];
            createAlt(Username, Accounttype);
        });
    } catch (error) {
        console.error(error);
    }
}

function bindAddAccountButton() {
    const addButton = document.querySelector('.add-account'); // Adjust this selector as needed
    if (addButton) {
        addButton.addEventListener('click', (e) => {
            e.preventDefault();

            let shouldCreate = false;
            let dropdownMenu = document.querySelector('.dropdown-menu');
            if (dropdownMenu == null) {
                console.log('creating')
                shouldCreate = true;
                // Create the dropdown menu
                dropdownMenu = document.createElement('div');
                dropdownMenu.className = 'dropdown-menu';
                // Append the dropdown menu to the "Add Account" button
                addButton.appendChild(dropdownMenu);

            }
            console.log(shouldCreate)

            // Create the "Offline" option
            const offlineOption = document.createElement('a');
            offlineOption.href = '#';
            offlineOption.textContent = 'Offline';
            offlineOption.addEventListener('click', async (e) => {
                e.preventDefault();

                // Create a card to contain the input box and buttons
                const card = document.createElement('div');
                card.className = 'card';
                document.body.appendChild(card);

                // Create an input box in the middle of the screen
                const inputBox = document.createElement('div');
                inputBox.innerHTML = `
                    <div class="input-container">
                        <input type="text" id="input" required="">
                        <label for="input" class="label">Enter Username</label>
                        <div class="underline"></div>
                    </div>
                `;
                card.appendChild(inputBox);

                // Create a div to contain the buttons
                const cardButtons = document.createElement('div');
                cardButtons.className = 'card-buttons';
                card.appendChild(cardButtons);

                // Create confirm and cancel buttons
                const confirmButton = document.createElement('button');
                confirmButton.textContent = 'Confirm';
                cardButtons.appendChild(confirmButton);

                const cancelButton = document.createElement('button');
                cancelButton.textContent = 'Cancel';
                cardButtons.appendChild(cancelButton);

                // Add event listeners to the buttons
                confirmButton.addEventListener('click', async () => {
                    // Call the API with the additional parameters
                    // Remove the card after use
                    document.body.removeChild(card);
                });

                cancelButton.addEventListener('click', () => {
                    // Remove the card without making the API call
                    document.body.removeChild(card);
                });
            });

            // Create the "Microsoft" option
            const microsoftOption = document.createElement('a');
            microsoftOption.href = '#';
            microsoftOption.textContent = 'Microsoft';
            microsoftOption.addEventListener('click', async (e) => {
                e.preventDefault();
                // Call the API with the additional parameters
                try {
                    const response = await fetch(`http://localhost:${port}/api/AddAlt?type=microsoft&username=neil_huang007`, {
                        method: 'GET',
                    });
                    const data = await response.json();
                    if (data.success) {
                        updateAlts(); // Refresh the alt list
                    } else {
                        console.error('Failed to add alt:', data.reason);
                    }
                } catch (error) {
                    console.error('Error adding alt:', error);
                }
            });

            addButton.addEventListener('mouseover', (e) => {
                e.preventDefault();
                isHovered = true;
                dropdownMenu.style.display = 'block';
                clearTimeout(hoverTimeout);
            });

            addButton.addEventListener('mouseout', (e) => {
                e.preventDefault();
                isHovered = false;
                hoverTimeout = setTimeout(() => {
                    if (!isHovered) {
                        dropdownMenu.style.display = 'none';
                    }
                }, 1500);
            });

            dropdownMenu.addEventListener('mouseover', (e) => {
                e.preventDefault();
                isHovered = true;
                clearTimeout(hoverTimeout);
            });

            dropdownMenu.addEventListener('mouseout', (e) => {
                e.preventDefault();
                isHovered = false;
                hoverTimeout = setTimeout(() => {
                    if (!isHovered) {
                        dropdownMenu.style.display = 'none';
                    }
                }, 1500);
            });
            if (shouldCreate) {
                // Append the options to the dropdown menu
                dropdownMenu.appendChild(offlineOption);
                dropdownMenu.appendChild(microsoftOption);
            }
        });

    }

}

function bindRandomButton() {
    const randomButton = document.querySelector('.random'); // Adjust this selector as needed
    randomButton.addEventListener('click', async (e) => {
        e.preventDefault();
        if (Object.keys(altsData).length > 0) {
            const randomAltKey = Object.keys(altsData)[Math.floor(Math.random() * Object.keys(altsData).length)];
            const alt = altsData[randomAltKey];
            const loginButton = document.querySelector(`[data-username="${alt.username}"]`);
            const altCard = loginButton.closest('.alt');
            await loginAlt(alt.username, loginButton, altCard);
        }
    });
}


async function init() {
    await updateAlts();
    bindAddAccountButton();
    bindRandomButton();
}

// Function to add a single module to the page

async function addModule(module) {
    const moduleContainer = document.querySelector('.module_container');

    const moduleElement = document.createElement('div');
    moduleElement.classList.add('module');

    const moduleContent = document.createElement('div');
    moduleContent.classList.add('module_content');

    const titleElement = document.createElement('h2');
    titleElement.textContent = module.name;
    moduleContent.appendChild(titleElement);

    const descriptionElement = document.createElement('p');
    descriptionElement.textContent = module.description;
    moduleContent.appendChild(descriptionElement);

    // Create the settings container for this module
    const settingsContainer = document.createElement('div');
    settingsContainer.className = 'settings-container';
    settingsContainer.style.display = 'none'; // Initially hidden
    moduleContent.appendChild(settingsContainer);

    // Check if the module is enabled and update UI
    // if (module.enabled) {
    //     console.log('Module is enabled:', module.name);
    //     updateModuleUI(moduleElement, module.name, true);
    // }

    let page = 1

    // Create a toggle button (initially 'Settings')
    const toggleButton = document.createElement('a');
    toggleButton.setAttribute('href', '#');
    toggleButton.textContent = 'Settings';
    toggleButton.addEventListener('click', function (event) {
        event.preventDefault();
        toggleModuleSettings(settingsContainer, module, titleElement, descriptionElement, toggleButton, page);
    });

    const ModuletoggleButton = document.createElement('a');
    ModuletoggleButton.setAttribute('href', '#');
    console.log(`module name: ${module.name} module enabled: ${module.enabled}`)
    ModuletoggleButton.textContent = module.enabled ? 'Toggled' : 'UnToggled';
    ModuletoggleButton.style.marginLeft = '10px';

    ModuletoggleButton.addEventListener('click', function (event) {
        event.preventDefault();
        module.enabled = !module.enabled; // Update module.enabled property
        toggleModuleState(module.name, module.enabled);
        ModuletoggleButton.textContent = module.enabled ? 'Toggled' : 'UnToggled';
        updateModuleUI(moduleElement, module.name, module.enabled); // Optional: Refresh module UI
    });

    // Fetch module settings from the API
    const response = await fetch(`http://localhost:${port}/api/getModuleSetting?module=${module.name}`);
    const settingsData = await response.json();

    if (settingsData.success) {
        const pageindecation = document.createElement('div');
        pageindecation.id = 'pageIndicator';
        // Calculate the total number of pages
        const settingsPerPage = 5; // Maximum number of settings per page
        const totalSettings = settingsData.result.length; // Total number of settings
        let totalPages = Math.ceil(totalSettings / settingsPerPage); // Total number of pages

        // Create navigation arrows dynamically
        const leftArrow = document.createElement('href');
        leftArrow.textContent = '<';
        leftArrow.style.cursor = 'pointer';
        leftArrow.id = 'leftArrow';
        leftArrow.style.position = 'relative'
        leftArrow.style.display = 'flex'
        // Add styles or classes to leftArrow as needed

        const rightArrow = document.createElement('href');
        rightArrow.textContent = '>';
        rightArrow.style.cursor = 'pointer';
        rightArrow.id = 'rightArrow';
        rightArrow.style.position = 'relative'
        rightArrow.style.display = 'flex'
        // Add styles or classes to rightArrow as needed

        // Append arrows and the page indicator to your module's UI
        pageindecation.appendChild(leftArrow);
        // Your totalPagesElement or a similar element for current page indicator
        const pagenumberindecator = document.createElement('span');
        pagenumberindecator.textContent = `Page ${page} of ${totalPages}`;
        pagenumberindecator.id = 'indecator';
        pagenumberindecator.style.display = 'flex';
        pagenumberindecator.style.position = 'relative';

        // Pagination controls
        leftArrow.addEventListener('click', () => {
            if (page > 1) {
                page--;
                pagenumberindecator.textContent = `Page ${page} of ${totalPages}`;
                populateSettings(settingsContainer, module, page);
            }
        });

        rightArrow.addEventListener('click', () => {
            if (page < totalPages) {
                page++;
                pagenumberindecator.textContent = `Page ${page} of ${totalPages}`;
                populateSettings(settingsContainer, module, page);
            }
        });

        pageindecation.appendChild(pagenumberindecator);
        pageindecation.appendChild(rightArrow);
        moduleContent.appendChild(pageindecation)
        moduleContent.appendChild(toggleButton);
        moduleContent.appendChild(ModuletoggleButton);
    } else {
        console.error('Failed to load settings:', settingsData.message);
        moduleContent.appendChild(toggleButton);
        moduleContent.appendChild(ModuletoggleButton);
    }

    moduleElement.appendChild(moduleContent);
    moduleContainer.appendChild(moduleElement);
}

function toggleModuleSettings(settingsContainer, module, titleElement, descriptionElement, toggleButton, settingpage) {
    const isSettingsVisible = settingsContainer.style.display !== 'none';

    // Toggle visibility of title, description, and change button text
    titleElement.style.display = isSettingsVisible ? 'block' : 'none';
    descriptionElement.style.display = isSettingsVisible ? 'block' : 'none';
    toggleButton.textContent = isSettingsVisible ? 'Settings' : 'Back';

    // Populate settings if they are about to be shown
    if (!isSettingsVisible) {
        populateSettings(settingsContainer, module, settingpage);
    }

    // Toggle settings container visibility
    settingsContainer.style.display = isSettingsVisible ? 'none' : 'block';
}

function populateSettings(settingsContainer, module, page) {
    settingsContainer.innerHTML = ''; // Clear previous content

    // Create settings title
    const settingsTitle = document.createElement('h2');
    settingsTitle.textContent = module.name + ' Settings';
    settingsContainer.appendChild(settingsTitle);

    // TODO: Create and append module-specific settings here
    // Create a new div element (you can customize this div as needed)
    const customDiv = document.createElement('div');
    customDiv.className = "settings_content"
    customDiv.textContent = 'This is a custom div between the module name and the back button.';
    settingsContainer.appendChild(customDiv);

    loadSettings(module.name, page, customDiv)
}


// Fetch and add modules based on the API response
async function loadModules(category) {
    try {
        // Clear existing modules
        const moduleContainer = document.querySelector('.module_container');
        moduleContainer.innerHTML = '';

        const response = await fetch(`http://localhost:${port}/api/modulesList?category=${category}`);
        const modulesData = await response.json();

        // Assuming modulesData is an object with module names as keys
        Object.keys(modulesData).forEach(moduleName => {
            const module = modulesData[moduleName];
            addModule(module);
        });
    } catch (error) {
        console.error('Error fetching modules:', error);
    }
}

async function loadSettings(moduleName, page, settingsContainer) {
    let currentPage = page;
    let settingsPerPage = 5;

    settingsContainer.innerHTML = ''; // Clear existing content

    const response = await fetch(`http://localhost:${port}/api/getModuleSetting?module=${moduleName}`);
    const settingsData = await response.json();

    if (settingsData.success) {
        const totalSettings = settingsData.result.length;
        const totalPages = Math.ceil(totalSettings / settingsPerPage);

        // Function to render settings for the current page
        function renderSettingsForPage(page) {
            const start = (page) * settingsPerPage - 5;
            const end = start + settingsPerPage;
            const settingsToRender = settingsData.result.slice(start, end);

            console.log('Rendering page:', page + 'start:', start, 'end:', end, 'settings:', settingsToRender.length)

            // Clear current settings
            settingsContainer.innerHTML = '';

            // Add each setting to the container
            settingsToRender.forEach(setting => {
                console.log(setting.name)
                createSettingElement(setting, settingsContainer, moduleName);
            });
        }

        // Render the current page for the module
        renderSettingsForPage(page);
    } else {
        console.error('Failed to load settings:', settingsData.message);
    }
}

function createBooleanSetting(setting, container, moduleName) {
    const settingElement = document.createElement('div');
    settingElement.className = 'setting';
    settingElement.style.display = 'flex';
    settingElement.style.justifyContent = 'space-between';
    settingElement.style.alignItems = 'center';
    settingElement.style.width = '100%'; // Ensure the setting element takes the full width

    const label = document.createElement('label');
    label.textContent = setting.name;
    label.style.flexGrow = 1; // Allow the label to take up available space

    // Create the toggle switch
    const toggleLabel = document.createElement('label');
    toggleLabel.className = 'toggle-switch';

    const checkbox = document.createElement('input');
    checkbox.setAttribute('type', 'checkbox');
    checkbox.checked = setting.value;

    const toggleBackground = document.createElement('div');
    toggleBackground.className = 'toggle-switch-background';

    const toggleHandle = document.createElement('div');
    toggleHandle.className = 'toggle-switch-handle';

    checkbox.addEventListener('change', () => {
        updateModuleSettings(moduleName, setting.name, checkbox.checked);
    });

    toggleBackground.appendChild(toggleHandle);
    toggleLabel.appendChild(checkbox);
    toggleLabel.appendChild(toggleBackground);

    settingElement.appendChild(label);
    settingElement.appendChild(toggleLabel);
    container.appendChild(settingElement);
}


// ... other functions ...

function createSliderSetting(setting, container, moduleName) {
    let hideIndicatorTimeout;
    const settingElement = document.createElement('div');
    settingElement.className = 'setting';

    const label = document.createElement('label');
    label.textContent = setting.name;
    settingElement.appendChild(label);

    const field = document.createElement('div');
    field.className = 'field';
    settingElement.appendChild(field);

    const minValueLabel = document.createElement('div');
    minValueLabel.className = 'value left';
    minValueLabel.textContent = setting.min;

    field.appendChild(minValueLabel);

    const slider = document.createElement('input');
    slider.type = 'range';
    slider.min = setting.min;
    slider.max = setting.max;
    slider.value = setting.value;
    slider.step = setting.step || 1;
    field.appendChild(slider);

    const progressBar = document.createElement('div');
    progressBar.className = 'progress-bar';
    field.appendChild(progressBar);

    const slideValue = document.createElement('span');
    slideValue.className = 'sliderValue';
    field.appendChild(slideValue);

    const maxValueLabel = document.createElement('div');
    maxValueLabel.className = 'value right';
    maxValueLabel.textContent = setting.max;
    field.appendChild(maxValueLabel);

    const updateProgressBar = () => {
        const value = slider.value;
        const valueMin = slider.min;
        const valueMax = slider.max;
        const totalInputWidth = slider.offsetWidth;
        const thumbHalfWidth = 10;
        const minValue = slider.min;
        const maxValue = slider.max;
        const left = (((value - minValue) / (valueMax - valueMin)) * ((totalInputWidth - thumbHalfWidth) - thumbHalfWidth)) + thumbHalfWidth;
        const width = slider.value / (slider.max - slider.min) * slider.offsetWidth;
        progressBar.style.left = slider.offsetLeft + 'px';
        if (value === minValue) {
            progressBar.style.width = 0 + 'px';
        } else {
            progressBar.style.width = (left - 5) + 'px';
        }

    }
    slider.oninput = updateProgressBar;

    const updateValueIndicator = () => {
        slideValue.textContent = slider.value;
        const percentage = ((slider.value - slider.min) / (slider.max - slider.min)) * 100;

        // Adjust the progress bar's width to align with the center of the thumb
        const thumbOffset = 10; // Half of the thumb's width
        const progressBarWidth = Math.min(percentage, 100) * (slider.offsetWidth - thumbOffset * 2) / 100 + thumbOffset - 7;

        slideValue.style.left = `calc(${progressBarWidth}px + 40px)`;
        slideValue.classList.add("show");
    };

    function hideValueIndicator() {
        slideValue.classList.remove("show");
    }

    slider.oninput = () => {
        updateProgressBar();
        updateValueIndicator();

        clearTimeout(hideIndicatorTimeout);
        hideIndicatorTimeout = setTimeout(hideValueIndicator, 1000); // Hide after 1 second of inactivity
        updateModuleSettings(moduleName, setting.name, slider.value);
    };


    container.appendChild(settingElement);

    updateProgressBar();
    updateValueIndicator();
    hideValueIndicator()
}

// ... other functions ...


function createRangeSliderSetting(setting, container, moduleName) {
    // Create a div that will act as the setting box
    const settingBox = document.createElement('div');
    settingBox.className = 'setting';

    // Create and add label
    const label = document.createElement('label');
    label.textContent = setting.name;
    label.className = 'setting-label'; // Optionally add a class for styling
    settingBox.appendChild(label);

    // Create container for range slider setting
    const rangeContainer = document.createElement('div');
    rangeContainer.className = 'rangevalue_wrapper';
    settingBox.appendChild(rangeContainer);

    // Create inner container
    const innerContainer = document.createElement('div');
    innerContainer.className = 'container';
    rangeContainer.appendChild(innerContainer);

    // Create min value display
    const minValueDisplay = document.createElement('div');
    minValueDisplay.className = 'min-value numberVal';
    const minValueInput = document.createElement('input');
    minValueInput.type = 'number';
    minValueInput.min = setting.min;
    minValueInput.max = setting.max;
    minValueInput.value = setting.minvalue;
    minValueInput.disabled = true;
    minValueDisplay.appendChild(minValueInput);
    innerContainer.appendChild(minValueDisplay);

    // Create range slider
    const rangeSlider = document.createElement('div');
    rangeSlider.className = 'range-slider';
    innerContainer.appendChild(rangeSlider);

    // Create progress bar
    const progressBar = document.createElement('div');
    progressBar.className = 'progress';
    rangeSlider.appendChild(progressBar);

    // Create range inputs
    const minRange = document.createElement('input');
    minRange.type = 'range';
    minRange.className = 'range-min';
    minRange.min = setting.min;
    minRange.max = setting.max;
    minRange.value = setting.minvalue;

    const maxRange = document.createElement('input');
    maxRange.type = 'range';
    maxRange.className = 'range-max';
    maxRange.min = setting.min;
    maxRange.max = setting.max;
    maxRange.value = setting.maxvalue;

    rangeSlider.appendChild(minRange);
    rangeSlider.appendChild(maxRange);

    // Create max value display
    const maxValueDisplay = document.createElement('div');
    maxValueDisplay.className = 'max-value numberVal';
    const maxValueInput = document.createElement('input');
    maxValueInput.type = 'number';
    maxValueInput.min = setting.min;
    maxValueInput.max = setting.max;
    maxValueInput.value = setting.maxvalue;
    maxValueInput.disabled = true;
    maxValueDisplay.appendChild(maxValueInput);
    innerContainer.appendChild(maxValueDisplay);

    // Add event listeners to range inputs
    minRange.addEventListener('input', () => {
        updateRangeSlider(minRange, maxRange, progressBar, minValueInput, maxValueInput);
        // Update module setting with option indicating 'min' value change
        updateModuleSettings(moduleName, setting.name, minRange.value, 'min');
    });

    maxRange.addEventListener('input', () => {
        updateRangeSlider(minRange, maxRange, progressBar, minValueInput, maxValueInput);
        // Update module setting with option indicating 'max' value change
        updateModuleSettings(moduleName, setting.name, maxRange.value, 'max');
    });
    // Initial update of the range slider
    updateRangeSlider(minRange, maxRange, progressBar, minValueInput, maxValueInput);

    // Append the setting box to the main container
    container.appendChild(settingBox);
}


function updateRangeSlider(minInput, maxInput, progress, minValueInput, maxValueInput) {
    console.log("update range slider")
    let minVal = parseFloat(minInput.value);
    let maxVal = parseFloat(maxInput.value);

    if (maxVal < minVal) {
        maxVal = minVal;
        maxInput.value = maxVal;
    }

    let totalRange = parseFloat(maxInput.max) - parseFloat(minInput.min);
    let minPercent = ((minVal - parseFloat(minInput.min)) / totalRange) * 100;
    let maxPercent = ((maxVal - parseFloat(minInput.min)) / totalRange) * 100;

    progress.style.left = minPercent + '%';
    progress.style.right = (100 - maxPercent) + '%';

    console.log("style left:", progress.style.left, "style right:", progress.style.right);

    console.log("Min Percent:", minPercent, "Max Percent:", maxPercent);
    minValueInput.value = minVal;
    maxValueInput.value = maxVal;
}


function createInputSetting(setting, container, moduleName) {
    // Create the wrapper div for the setting
    const settingWrapper = document.createElement('div');
    settingWrapper.className = 'setting';

    // Create the label or name of the setting
    const settingName = document.createElement('div');
    settingName.className = 'setting-name';
    settingName.textContent = setting.name;
    settingWrapper.appendChild(settingName);

    // Create the form control container
    const formControl = document.createElement('div');
    formControl.className = 'form-control';

    // Create and add the input element
    const input = document.createElement('input');
    input.className = 'input';
    input.type = 'text';
    input.placeholder = setting.placeholder || 'Enter text'; // Example placeholder
    input.required = setting.required || false;
    formControl.appendChild(input);

    // Create and add the focus indicator div
    const focusIndicator = document.createElement('div');
    focusIndicator.className = 'input-focus-indicator';

    input.addEventListener('change', () => {
        updateModuleSettings(moduleName, setting.name, input.value);
    });

    formControl.appendChild(focusIndicator);

    // Append the form control to the wrapper
    settingWrapper.appendChild(formControl);

    // Append the setting wrapper to the container
    container.appendChild(settingWrapper);
}

function CreateListSetting(setting, container, moduleName) {
    // Create a div that will act as the setting box
    const settingBox = document.createElement('div');
    settingBox.className = 'setting';

    // Create a label for the setting name
    const settingLabel = document.createElement('div');
    settingLabel.className = 'setting-name';
    settingLabel.textContent = setting.name;
    settingBox.appendChild(settingLabel);

    // Create container for radio buttons
    const radioButtonContainer = document.createElement('div');
    radioButtonContainer.className = 'radio-button-container';

    // Loop through each option in the setting
    setting.values.forEach((option, index) => {
        // Create a div for each radio button
        const radioButtonDiv = document.createElement('div');
        radioButtonDiv.className = 'radio-button';

        // Create the radio input
        const radioButtonInput = document.createElement('input');
        radioButtonInput.type = 'radio';
        radioButtonInput.className = 'radio-button__input';
        radioButtonInput.id = setting.name + index;
        radioButtonInput.name = setting.name;
        radioButtonInput.value = option;
        if (setting.value === option) { // Set the radio button as checked if it matches the setting value
            radioButtonInput.checked = true;
        }

        // Add event listener for radio button change
        radioButtonInput.addEventListener('change', () => {
            updateModuleSettings(moduleName, setting.name, option);
        });

        // Create the label for the radio button
        const radioButtonLabel = document.createElement('label');
        radioButtonLabel.className = 'radio-button__label';
        radioButtonLabel.htmlFor = setting.name + index;

        // Create the custom radio button
        const radioButtonCustom = document.createElement('span');
        radioButtonCustom.className = 'radio-button__custom';

        // Append elements to the radio button div
        radioButtonLabel.appendChild(radioButtonCustom);
        radioButtonLabel.appendChild(document.createTextNode(option));
        radioButtonDiv.appendChild(radioButtonInput);
        radioButtonDiv.appendChild(radioButtonLabel);

        // Append the radio button div to the container
        radioButtonContainer.appendChild(radioButtonDiv);
    });

    // Append the radio button container to the setting box
    settingBox.appendChild(radioButtonContainer);

    // Append the setting box to the main container
    container.appendChild(settingBox);
}

function createModeSetting(setting, container, moduleName) {
    const settingElement = document.createElement('div');
    settingElement.className = 'setting mode-setting';

    const label = document.createElement('label');
    label.className = 'mode-name';
    label.textContent = setting.name;
    settingElement.appendChild(label);

    const modeControl = document.createElement('div');
    modeControl.className = 'mode-control';

    const leftArrow = document.createElement('div');
    leftArrow.className = 'arrow';
    leftArrow.textContent = '<';
    modeControl.appendChild(leftArrow);

    const modeValue = document.createElement('div');
    modeValue.className = 'mode-value';
    modeValue.textContent = setting.value;
    modeControl.appendChild(modeValue);

    const rightArrow = document.createElement('div');
    rightArrow.className = 'arrow';
    rightArrow.textContent = '>';
    modeControl.appendChild(rightArrow);

    settingElement.appendChild(modeControl);
    container.appendChild(settingElement);

    let currentIndex = setting.values.indexOf(setting.value);

    const updateModeValue = (direction) => {
        if (direction === 'left') {
            currentIndex = (currentIndex - 1 + setting.values.length) % setting.values.length;
        } else {
            currentIndex = (currentIndex + 1) % setting.values.length;
        }
        modeValue.textContent = setting.values[currentIndex];
        updateModuleSettings(moduleName, setting.name, setting.values[currentIndex]);
    };

    leftArrow.addEventListener('click', () => updateModeValue('left'));
    rightArrow.addEventListener('click', () => updateModeValue('right'));
}

function createColorSetting(setting, container, moduleName) {
    // Create the container element for the color setting
    const settingElement = document.createElement('div');
    settingElement.className = 'setting color-setting';

    // Create the label element for the setting name
    const label = document.createElement('label');
    label.className = 'color-setting-name';
    label.textContent = setting.name;
    settingElement.appendChild(label);

    // Create a new div block to wrap the color picker
    const colorPickerWrapper = document.createElement('div');
    colorPickerWrapper.style.display = 'flex';
    settingElement.appendChild(colorPickerWrapper);

    // Create the color picker element
    const colorPicker = document.createElement('div');
    colorPicker.className = 'color-picker';
    colorPickerWrapper.appendChild(colorPicker);

    // Set the default color based on the setting value (if available)
    // let defaultColor = '#42445a'; // Default color
    // if (setting.value) {
    //     defaultColor = ;
    // }

    // Convert the RGBA array to a string
    let defaultColor = 'rgba(' + `${setting.value[0]}, ${setting.value[1]}, ${setting.value[2]}` + ', 1)';
    console.log(`Default color: ${defaultColor}`);

    // Initialize the color picker
    const pickr = Pickr.create({
        el: colorPicker,
        theme: 'monolith',
        default: defaultColor,
        defaultRepresentation: 'RGBA',
        components: {
            preview: true,
            opacity: true,
            hue: true,
            interaction: {
                hex: true,
                rgba: true,
                hsla: true,
                hsva: true,
                cmyk: false,
                input: true,
                clear: true,
                save: true
            }
        }
    });

    // Update the color setting value when the color picker is closed
    pickr.on('save', (color) => {
        const rgbaColor = color.toRGBA();
        updateModuleSettings(moduleName, setting.name, [rgbaColor[0], rgbaColor[1], rgbaColor[2], rgbaColor[3]]);
        pickr.hide();
    });

    container.appendChild(settingElement);
}


function createSettingElement(setting, container, moduleName) {
    switch (setting.type) {
        case 'checkbox':
            createBooleanSetting(setting, container, moduleName);
            break;
        case 'slider':
            createSliderSetting(setting, container, moduleName);
            break;
        case 'range_slider':
            createRangeSliderSetting(setting, container, moduleName);
            break;
        case 'input':
            createInputSetting(setting, container, moduleName);
            break;
        case 'radio':
            CreateListSetting(setting, container, moduleName);
            break;
        // Add cases for other types as needed
        case 'color':
            createColorSetting(setting, container, moduleName);
            break;
        case 'mode':
            createModeSetting(setting, container, moduleName);
            break;

    }
}


document.addEventListener('DOMContentLoaded', function () {
    const categoryButtons = document.querySelectorAll('.catagory_name');
    categoryButtons.forEach(button => {
        button.addEventListener('click', function () {
            const category = this.textContent; // Assuming the category name is the same as expected by the API
            /*if (category === 'Alt Manager') {
                const mainContentArea = document.querySelector('.module_container');
                mainContentArea.innerHTML = ''; // Clear the main content area

                // Create the Alt Manager page
                const altManagerPage = document.createElement('div');
                altManagerPage.innerHTML = `
                    <div class="altmanager">
                        <div class="AltManager_Title">
                            Alt Account Manager
                        </div>
                        <div class="buttons-panel">
                            <a href="#" class="add-account">+ Add Account</a>
                            <a href="#" class="random" style="margin-left: 10px;">Random</a>
                        </div>
                        <div class="alt-display"></div>
                    </div>
                `;

                // Append the Alt Manager page to the main content area
                mainContentArea.appendChild(altManagerPage);

                init(); // Call the initialization function to set everything up

            } else*/
            {
                loadModules(category);
            }
        });
    });
    // Initial load with default category
    loadModules('Render');
});

async function updateModuleSettings(moduleName, settingName, settingValue, options = "none") {
    // Construct the URL with query parameters
    let url = `http://localhost:${port}/api/setModuleSettingValue?module=${encodeURIComponent(moduleName)}&name=${encodeURIComponent(settingName)}&value=${encodeURIComponent(settingValue)}&options=${encodeURIComponent(options)}`;

    try {
        const response = await fetch(url);
        const data = await response.json();

        if (data.success) {
            console.log('Setting updated successfully:', data.result);
            // Perform any additional actions based on the response
        } else {
            console.error('Error updating setting:', data.reason);
            alert(data.reason);
        }
    } catch (error) {
        console.error('Error in updateModuleSettings:', error);
        alert("Error: " + error);
    }
}

// Example usage (assuming you have a specific setting and module name)
// updateModuleSettings('ModuleName', 'SettingName', 'NewValue', 'SettingType', { optionKey: 'optionValue' });


async function toggleModuleState(moduleName, isEnabled) {
    const url = `http://localhost:${port}/api/updateModulesInfo?displayname=${encodeURIComponent(moduleName)}&enable=${isEnabled}`;

    try {
        const response = await fetch(url);
        const data = await response.json();
        if (data.success) {
            console.log('Module state updated successfully');
            return !isEnabled; // Return the new state
        } else {
            console.error('Error updating module state:', data.reason);
        }
    } catch (error) {
        console.error('Error in toggleModuleState:', error);
    }
}

function updateModuleUI(moduleElement, moduleName, isEnabled) {
    /*cconst titleElement = document.querySelector('.module_content h2');

    onsole.log("aa")
    if (isEnabled) {
        titleElement.style.content += '(enabled)';
    } else {
        titleElement.style.color = 'White';
    }*/
}
