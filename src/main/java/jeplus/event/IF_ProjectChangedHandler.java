/*
 * Copyright (C) 2019 yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jeplus.event;

import jeplus.JEPlusProjectV2;

/**
 *
 * @author yi
 */
public interface IF_ProjectChangedHandler {
    public abstract void projectChanged (JEPlusProjectV2 new_prj);
    public abstract void projectSaved (String filename);
}
